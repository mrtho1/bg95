package com.thompson234.bg95.model;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class SearchResult {

    private String _query;
    private List<AirmanSummary> _airmen = Lists.newArrayList();
    private List<AircraftSummary> _aircraft = Lists.newArrayList();
    private List<MissionSummary> _missions = Lists.newArrayList();

    public SearchResult() {
    }

    public SearchResult(String query) {
        setQuery(query);
    }

    public String getQuery() {
        return _query;
    }

    public void setQuery(String query) {
        _query = query;
    }

    public SearchResult query(String query) {
        setQuery(query);
        return this;
    }

    public List<AirmanSummary> getAirmen() {
        return _airmen;
    }

    public void setAirmen(Collection<AirmanSummary> airmen) {
        _airmen.clear();
        _airmen.addAll(airmen);
    }

    public SearchResult airmen(Collection<AirmanSummary> airmen) {
        setAirmen(airmen);
        return this;
    }

    public SearchResult airman(AirmanSummary airman) {
        _airmen.add(airman);
        return this;
    }

    public List<AircraftSummary> getAircraft() {
        return _aircraft;
    }

    public void setAircraft(Collection<AircraftSummary> aircraft) {
        _aircraft.clear();
        _aircraft.addAll(aircraft);
    }

    public SearchResult aircraft(Collection<AircraftSummary> aircraft) {
        setAircraft(aircraft);
        return this;
    }

    public SearchResult aircraft(AircraftSummary aircraft) {
        _aircraft.add(aircraft);
        return this;
    }

    public List<MissionSummary> getMissions() {
        return _missions;
    }

    public void setMissions(Collection<MissionSummary> missions) {
        _missions.clear();
        _missions.addAll(missions);
    }

    public SearchResult missions(Collection<MissionSummary> missions) {
        setMissions(missions);
        return this;
    }

    public SearchResult mission(MissionSummary mission) {
        _missions.add(mission);
        return this;
    }
}
