Bg95App.loadModelSummaries = function() {

  console.log("Loading model summaries from server.");

  console.log("Loading aircraft summaries from server.");
  $.ajax({
    url: "api/aircraft",
    dataType: 'json',
    success: function(data) {
        var summaries = data.map(function(rawSummary) {
            rawSummary.type = "Bg95App.AircraftSummary";
            rawSummary.title = rawSummary.number;

            if (rawSummary.names !== null && rawSummary.names.length > 0) {
                rawSummary.title = rawSummary.names.join("/");
            }

            return rawSummary;
        });

        Bg95App.summaryController.set('aircraft', summaries);
        console.log("Successfully pulled aircraft summaries from server.");
    }
  });

  console.log("Loading airmen summaries from server.");
  $.ajax({
    url: "api/airman",
    dataType: 'json',
    success: function(data) {
        var summaries = data.map(function(rawSummary) {
            rawSummary.type = "Bg95App.AirmanSummary";
            rawSummary.title = rawSummary.rank + " " + rawSummary.label;
            return rawSummary;
        });

        Bg95App.summaryController.set('airmen', summaries);
        console.log("Successfully pulled airmen summaries from server.");
    }
  });

  console.log("Loading mission summaries from server.");
  $.ajax({
    url: "api/mission",
    dataType: 'json',
    success: function(data) {
        var summaries = data.map(function(rawSummary) {
            rawSummary.type = "Bg95App.MissionSummary";
            var date = new Date(rawSummary.date);
            rawSummary.title = rawSummary.number + " " + date + " " + rawSummary.label;
            return rawSummary;
        });

        Bg95App.summaryController.set('missions', summaries);
        console.log("Successfully pulled mission summaries from server.");
    }
  });
}

Bg95App.summaryController = Em.Object.create({
    aircraft: [],
    airmen: [],
    missions: [],

    aircraftCount: function() {
        return this.get('aircraft').length;
    }.property('aircraft'),

    airmanCount: function() {
        return this.get('airmen').length;
    }.property('airmen'),

    missionCount: function() {
        return this.get('missions').length;
    }.property('missions'),
});
