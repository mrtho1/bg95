
var aircraftStore = new Lawnchair({name: 'aircraft', record: 'aircraft'}, function() {
});

var airmanStore = new Lawnchair({name: 'airmen', record: 'airman'}, function() {
});

var missionStore = new Lawnchair({name: 'missions', record: 'mission'}, function() {
});

var BG95App = Em.Application.create({
    ready: function() {
        this._super();
        window.scrollTo(0);
        BG95App.GetDataFromLocalStores();
    }
});

BG95App.GetDataFromLocalStores = function() {

    var aircraft = aircraftStore.all(function(arr) {
        arr.forEach(function(rawAircraft) {
            var aircraft = BG95app.Aircraft.create(rawAircraft);
            BG95App.aircraftController.addAircraft(aircraft);
        });

        console.log("Aircraft loaded from local data store:", arr.length);
        BG95App.GetAircraftFromServer();
    });

    var airmen = airmanStore.all(function(arr) {
        arr.forEach(function(rawAirman) {
            var airman = BG95app.Airman.create(rawAirman);
            BG95App.airmanController.addAirman(airman);
        });

        console.log("Airmen loaded from local data store:", arr.length);
        BG95App.GetAirmenFromServer();
    });

    var missions = missionStore.all(function(arr) {
        arr.forEach(function(rawMission) {
            var mission = BG95app.Mission.create(rawMission);
            BG95App.missionController.addMission(mission);
        });

        console.log("Missions loaded from local data store:", arr.length);
        BG95App.GetMissionsFromServer();
    });
};

BG95App.GetAircraftFromServer = function() {
    console.log("Starting AJAX Request for aircraft.");

    $.ajax({
        url: "/api/aircraft",
        dataType: "json",
        success: function(data) {
            data.map(function(rawAircraft) {
                var aircraft = BG95App.Aircraft.create(rawAircraft);
                if (BG95App.aircraftController.addAircraft(aircraft)) {
                    aircraftStore.save(rawAircraft);
                }
            });

            console.log("Aircraft successfully loaded from server:", BG95App.aircraftController.get('length'));;
        }
    });
};

BG95App.GetAirmenFromServer = function() {
    console.log("Starting AJAX Request for airmen.");

    $.ajax({
        url: "/api/airman",
        dataType: "json",
        success: function(data) {
            data.map(function(rawAirman) {
                var airman = BG95App.Airman.create(rawAirman);
                if (BG95App.airmanController.addAirman(airman)) {
                    airmanStore.save(rawAirman);
                }
            });

            console.log("Airmen successfully loaded from server:", BG95App.airmanController.get('length'));
        }
    });
};

BG95App.GetMissionsFromServer = function() {
    console.log("Starting AJAX Request for missions.");

    $.ajax({
        url: "/api/mission",
        dataType: "json",
        success: function(data) {
            data.map(function(rawMission) {
                var mission = BG95App.Mission.create(rawMission);
                if (BG95App.missionController.addMission(mission)) {
                    missionStore.save(rawMission);
                }
            });

            console.log("Missions successfully loaded from server:", BG95App.missionController.get('length'));
        }
    });
};

BG95App.Aircraft = Em.Object.extend({
    id: null,
    number: null,
    names: null,
    squadrons: null,
    model: null,
    callsigns: null
});

BG95App.Airman = Em.Object.extend({
    id: null,
    fullName: null,
    ranks: null,
    roles: null,
    notes: null,
    units: null
});

BG95App.Mission = Em.Object.extend({
    id: null,
    number: null,
    date: null,
    destination: null,
    tookOff: 0,
    completed: 0,
    damaged: 0,
    lost: 0,
    salvaged: 0,
    sorties: null
});

BG95App.aircraftController = Em.ArrayController.create({
    content: [],
    addAircraft: function(aircraft) {
        var exists = this.filterProperty('id', aircraft.id).length;
        if (exists === 0) {
          this.insertAt(this.get('length'), aircraft);
          return true;
        } else {
          return false;
        }
      }
});

BG95App.airmanController = Em.ArrayController.create({
    content: [],
    addAirman: function(airman) {
        var exists = this.filterProperty('id', airman.id).length;
        if (exists === 0) {
          this.insertAt(this.get('length'), airman);
          return true;
        } else {
          return false;
        }
    }
});

BG95App.missionController = Em.ArrayController.create({
    content: [],
    addMission: function(mission) {
        var exists = this.filterProperty('id', mission.id).length;
        if (exists === 0) {
          this.insertAt(this.get('length'), mission);
          return true;
        } else {
          return false;
        }
    }
});
