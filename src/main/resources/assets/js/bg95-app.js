ENV = {
    VIEW_PRESERVES_CONTEXT: true,
    RAISE_ON_DEPRECATION: true
};

var store = new Lawnchair(function() {});

Handlebars.registerHelper('join', function(property) {
  var value = Ember.getPath(this, property);
  return value !== null ? value.join(", ") : "";
});

var Bg95App = Em.Application.create({
  ready: function() {
    this._super();

    //On mobile devices, hide the address bar
    window.scrollTo(0);
    Bg95App.countController.fetch();
    $(document).ready(function(){
        $("#searchField").focus();
    });
  },

  AIRMAN_TYPE: "airman",
  AIRCRAFT_TYPE: "aircraft",
  MISSION_TYPE: "mission"
});

Bg95App.formatDate = function(theDate) {
    var d = new Date(theDate);
    return moment(d).format("MMMM Do YYYY");
};

Bg95App.imageUrls = function(urls) {
    if (urls === null) {
        return urls;
    }

    return urls.map(function(imageUrl) {

        var result = {href: imageUrl, src: imageUrl};
        if (imageUrl.match(/html$/) || imageUrl.match(/htm$/))  {
            result.src = "http://images.w3snapshot.com/?size=S&key=bb539caee191df4a0ba1efbbc7b5d14b&url=" + imageUrl;
        }

        return result;
    });
};

//Object Model
Bg95App.Name = Em.Object.extend({
    salutation: null,
    firstName: null,
    lastName: null,
    middle: null,
    suffixes: [],

    fullName: function() {
        var tokens = [];

        var salutation = this.get('salutation');
        if (salutation !== null && salutation.length > 0) {
            tokens.push(salutation);
        }

        var firstName = this.get('firstName');
        if (firstName !== null && firstName.length > 0) {
            tokens.push(firstName);
        }

        var middle = this.get('middle');
        if (middle !== null && middle.length > 0) {
            tokens.push(middle + ".");
        }

        var lastName = this.get('lastName');
        if (lastName !== null && lastName.length > 0) {
            tokens.push(lastName);
        }

        var suffixes = this.get('suffixes');
        if (suffixes != null && suffixes.length > 0) {
            tokens = tokens.concat(suffixes);
        }

        return tokens.join(" ");
    }.property("salutation", "firstName", "lastName", "middle", "suffixes.@each")
});

Bg95App.Aircraft = Em.Object.extend({
    id: null,
    number: null,
    names: [],
    squadrons: [],
    callsigns: [],
    imageUrls: [],
    model: null,

    primaryName: function() {
        var names = this.get('names');
        return (names !== null && names.length > 0) ? names[0] : "";
    }.property("names.@each")
});

Bg95App.Airman = Em.Object.extend({
    id: null,
    name: null,
    imageUrls: [],
    ranks: [],
    roles: [],
    notes: [],
    units: [],

    init: function() {
        this._super();
        this.set('name', Bg95App.Name.create(this.get('name')));
    },

    fullName: function() {
        return this.get('name').get('fullName');
    }.property('name.fullName'),

    rank: function() {
        var allRanks = this.get('ranks');
        return (allRanks === null || allRanks.length === 0) ? "" : allRanks[allRanks.length - 1];
    }.property('ranks.@each')
});

Bg95App.CrewAssignment = Em.Object.extend({
    airman: null,
    role: null,
    status: null,

    init: function() {
        this._super();
        this.set('airman', Bg95App.Airman.create(this.get('airman')));
    },

    isPilot: function() {
       return this.get('role').match(/pilot/i);
    }.property('role')
});

Bg95App.Sortie = Em.Object.extend({
    aircraft: null,
    crewAssignments: [],

    init: function() {
        this._super();
        this.set('aircraft', Bg95App.Aircraft.create(this.get('aircraft')));

        this.set('crewAssignments', this.get('crewAssignments').map(function(rawAssignment) {
            return Bg95App.CrewAssignment.create(rawAssignment);
        }));
    }
});

Bg95App.Mission = Em.Object.extend({
    id: null,
    number: 0,
    date: new Date(0),
    destination: null,
    tookOff: 0,
    completed: 0,
    damaged: 0,
    lost: 0,
    salvaged: 0,
    sorties: [],

    init: function() {
        this._super();
        this.set('sorties', this.get('sorties').map(function(rawSortie) {
            return Bg95App.Sortie.create(rawSortie);
        }));
    }
});

Bg95App.SearchResult = Em.Object.extend({
    query: null,
    airmen: [],
    aircraft: [],
    missions: []
});

Bg95App.countController = Em.Object.create({
    airmanCount: 0,
    aircraftCount: 0,
    missionCount: 0,

    fetch: function() {

        console.log("Fetching object counts.");
        $.ajax({
            url: "api/count",
            dataType: 'json',
            success: function(data) {
                Bg95App.countController.set('airmanCount', data.airman);
                Bg95App.countController.set('aircraftCount', data.aircraft);
                Bg95App.countController.set('missionCount', data.mission);
                console.log("Object counts fetched successfully.");
            }
        });
    }
});

Bg95App.selectionController = Em.Object.create({

    selected: null,
    hasPrev: false,
    hasNext: false,

    select: function(item) {
        this.set('selected', item);
        if (item) {
            var currentIndex = Bg95App.searchController.indexOf(this.get('selected'));
            if (currentIndex + 1 >= Bg95App.searchController.get('itemCount')) {
                this.set('hasNext', false);
            } else {
                this.set('hasNext', true);
            }

            this.set('hasPrev', (currentIndex !== 0));
        } else {
            this.set('hasPrev', false);
            this.set('hasNext', false);
        }
    },

    next: function() {
        var currentIndex = Bg95App.searchController.indexOf(this.get('selected'));
        var nextItem = Bg95App.searchController.getContentAt(currentIndex + 1);

        if (nextItem) {
            this.select(nextItem);
        }
    },

    prev: function() {
        var currentIndex = Bg95App.searchController.indexOf(this.get('selected'));
        var prevItem = Bg95App.searchController.getContentAt(currentIndex - 1);

        if (prevItem) {
            this.select(prevItem);
        }
    }
});

Bg95App.searchController = Em.Object.create({
    query: "",
    searchResult: null,
    content: [],

    fetch: function() {
        console.log("Search Query: " + this.get('query'));
        $.ajax({
            url: "api/search?q=" + encodeURIComponent(this.get('query')),
            dataType: 'json',
            success: function(data) {
                Bg95App.searchController.set('searchResult', Bg95App.SearchResult.create(data));
                var content = [].concat(data.airmen, data.aircraft, data.missions);
                Bg95App.searchController.set('content', content);
                console.log("Successfully pulled search results from server.");
            }
        });
    },

    itemCount: function() {
        return this.get('content').length;
    }.property('content'),

    getContentAt: function(index) {
        return this.get('content')[index];
    },

    indexOf: function(item) {
        return this.get('content').indexOf(item);
    }
});

Bg95App.NavBarView = Em.View.extend({
    airmanCount: function() {
        return Bg95App.countController.get('airmanCount');
    }.property('Bg95App.countController.airmanCount'),

    aircraftCount: function() {
        return Bg95App.countController.get('aircraftCount');
    }.property('Bg95App.countController.aircraftCount'),

    missionCount: function() {
        return Bg95App.countController.get('missionCount');
    }.property('Bg95App.countController.missionCount'),

    refresh: function() {
        Bg95App.countController.fetch();
    },

    search: function(event) {
        Bg95App.searchController.fetch();
    }
});

Bg95App.SearchFieldView = Em.TextField.extend({
    classNames: "search-query",
    placeholder: "Search",
    valueBinding: "Bg95App.searchController.query",
    change: function() {
        Bg95App.searchController.fetch();
    }
})

// Left hand controls view
Bg95App.NavControlsView = Em.View.extend({
  tagName: 'section',

  classNames: ['controls'],

  // Click handler for up/previous button
  navUp: function(event) {
    Bg95App.selectionController.prev();
  },

  // Click handler for down/next button
  navDown: function(event) {
    Bg95App.selectionController.next();
  },

  nextDisabled: function() {
    return !Bg95App.selectionController.get('hasNext');
  }.property('Bg95App.selectionController.selected'),

  prevDisabled: function() {
    return !Bg95App.selectionController.get('hasPrev');
  }.property('Bg95App.selectionController.selected'),

  buttonDisabled: function() {
    var selected = Bg95App.selectionController.get('selected');
    if (selected) {
      return false;
    }
    return true;
  }.property('Bg95App.selectionController.selected')
});

// A special observer that will watch for when the 'selectedItem' is updated
// and ensure that we scroll into a view so that the selected item is visible
// in the summary list view.
Bg95App.selectionController.addObserver('selected', function() {
  var curScrollPos = $('.summaries').scrollTop();

  var offset = $('.summary.active').offset();
  var itemTop = (offset !== null) ? offset.top : 0;
  itemTop -= 60;

  $(".summaries").animate({"scrollTop": curScrollPos + itemTop}, 200);
});

Bg95App.searchController.addObserver('content', function() {
    var content = Bg95App.searchController.get('content');
    if (content.length > 0) {
        Bg95App.selectionController.select(content[0]);
    }
});

// View for the ItemsList
Bg95App.SummaryListView = Em.View.extend({
  tagName: 'article',
  classNames: ['well', 'summary'],
  classNameBindings: ['active', 'prev', 'next'],
  content: null,

  // Handle clicks on item summaries with the same code path that
  // handles the touch events.
  click: function(evt) {
    this.touchEnd(evt);
  },

  // Handle clicks/touch/taps on an item summary
  touchEnd: function(evt) {
    // Figure out what the user just clicked on, then set selectedItemController
    var content = this.get('content');
    Bg95App.selectionController.select(content);
  },

  // Enables/Disables the active CSS class
  active: function() {
    var selectedItem = Bg95App.selectionController.get('selected');
    var content = this.get('content');
    if (content === selectedItem) {
      return true;
    }
  }.property('Bg95App.selectionController.selected'),

    _isType: function(type) {
        var content = this.get('content');
        return content.type === type;
    },

    isAirman: function() {
        return this._isType(Bg95App.AIRMAN_TYPE);
    }.property('content.type'),

    isAircraft: function() {
        return this._isType(Bg95App.AIRCRAFT_TYPE);
    }.property('content.type'),

    isMission: function() {
        return this._isType(Bg95App.MISSION_TYPE);
    }.property('content.type')
});

Bg95App.AirmanSummaryListView = Em.View.extend({
    content: null,
    templateName: 'airman-summary-list-template'
});

Bg95App.AirmanDetailView = Em.View.extend({
    templateName: 'airman-detail-template',
    contentBinding: 'Bg95App.detailsController.content',
    classNames: ['well', 'entry'],

    imageUrls: function() {
        return Bg95App.imageUrls(this.getPath('content.imageUrls'));
    }.property('content.imageUrls.@each'),

    roles: function() {
        return this.getPath('content.roles').join(", ");
    }.property("content.roles.@each"),

    notes: function() {
        return this.getPath('content.notes').join(", ");
    }.property("content.notes.@each"),

    units: function() {
        return this.getPath('content.units').join(", ");
    }.property("content.units.@each"),
});

Bg95App.AircraftSummaryListView = Em.View.extend({
    content: null,
    templateName: 'aircraft-summary-list-template'
});

Bg95App.AircraftDetailView = Em.View.extend({
    templateName: 'aircraft-detail-template',
    contentBinding: 'Bg95App.detailsController.content',
    classNames: ['well', 'entry'],

    imageUrls: function() {
        return Bg95App.imageUrls(this.getPath('content.imageUrls'));
    }.property('content.imageUrls.@each'),

    names: function() {
        return this.getPath('content.names').join(", ");
    }.property("content.names.@each"),

    squadrons: function() {
        return this.getPath('content.squadrons').join(", ");
    }.property("content.squadrons.@each"),

    callsigns: function() {
        return this.getPath('content.callsigns').join(", ");
    }.property("content.callsigns.@each")
});

Bg95App.MissionSummaryListView = Em.View.extend({
    content: null,
    templateName: 'mission-summary-list-template',

    missionDate: function() {
        return Bg95App.formatDate(this.getPath('content.date'));
    }.property('content.date')
});

Bg95App.MissionDetailView = Em.View.extend({
    templateName: 'mission-detail-template',
    contentBinding: 'Bg95App.detailsController.content',
    classNames: ['well', 'entry'],

    missionDate: function() {
        return Bg95App.formatDate(this.getPath('content.date'));
    }.property('content.date')
});

Bg95App.detailsController = Em.Object.create({
    content: null,
    type: null,

    _isType: function(checkType) {
        var type = this.get('type');
        return type === checkType;
    },

    isAirman: function() {
        return this._isType(Bg95App.AIRMAN_TYPE);
    }.property('type'),

    isAircraft: function() {
        return this._isType(Bg95App.AIRCRAFT_TYPE);
    }.property('type'),

    isMission: function() {
        return this._isType(Bg95App.MISSION_TYPE);
    }.property('type'),

    fetch: function() {
        var selection = Bg95App.selectionController.get('selected');
        console.log("Fetching object detail for " + selection.id + ".");

        var currentDetail = Bg95App.detailsController.get('content');
        if (currentDetail !== null && currentDetail.get('id') === selection.id) {
            console.log("Details for " + selection.id + " already loaded.");
            return;
        }

        $.ajax({
            url: "api/" + selection.type + "/" + encodeURIComponent(selection.id),
            dataType: 'json',
            success: function(data) {
                var detail = null;
                if (selection.type == Bg95App.AIRMAN_TYPE) {
                    detail = Bg95App.Airman.create(data);
                } else if (selection.type == Bg95App.AIRCRAFT_TYPE) {
                    detail = Bg95App.Aircraft.create(data);
                } else if (selection.type == Bg95App.MISSION_TYPE) {
                    detail = Bg95App.Mission.create(data);
                } else {
                    Bg95App.detailsController.set('type', null);
                    console.log("Could not determine detail type.");
                }

                if (detail !== null) {
                    Bg95App.detailsController.set('type', selection.type);
                    Bg95App.detailsController.set('content', detail);
                    console.log("Detail fetched successfully.");
                    console.dir(detail);
                }
            }
        });
    }
});

Bg95App.selectionController.addObserver('selected', function() {
    Bg95App.detailsController.fetch();
});

Bg95App.detailsController.addObserver('content', function() {
  $(".entries").animate({"scrollTop": 0}, 200);
});
