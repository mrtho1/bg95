package com.thompson234.bg95.model;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SearchResult {

    private String _query;
    private List<Map<String, Object>> _airmen = Lists.newArrayList();
    private List<Map<String, Object>> _aircraft = Lists.newArrayList();
    private List<Map<String, Object>> _missions = Lists.newArrayList();

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

    public List<Map<String, Object>> getAirmen() {
        return _airmen;
    }

    public void setAirmen(Collection<Map<String, Object>> airmen) {
        _airmen.clear();
        _airmen.addAll(airmen);
    }

    public SearchResult airmen(Collection<Map<String, Object>> airmen) {
        setAirmen(airmen);
        return this;
    }

    public SearchResult airman(Map<String, Object> airman) {
        _airmen.add(airman);
        return this;
    }

    public List<Map<String, Object>> getAircraft() {
        return _aircraft;
    }

    public void setAircraft(Collection<Map<String, Object>> aircraft) {
        _aircraft.clear();
        _aircraft.addAll(aircraft);
    }

    public SearchResult aircraft(Collection<Map<String, Object>> aircraft) {
        setAircraft(aircraft);
        return this;
    }

    public SearchResult aircraft(Map<String, Object> aircraft) {
        _aircraft.add(aircraft);
        return this;
    }

    public List<Map<String, Object>> getMissions() {
        return _missions;
    }

    public void setMissions(Collection<Map<String, Object>> missions) {
        _missions.clear();
        _missions.addAll(missions);
    }

    public SearchResult missions(Collection<Map<String, Object>> missions) {
        setMissions(missions);
        return this;
    }

    public SearchResult mission(Map<String, Object> mission) {
        _missions.add(mission);
        return this;
    }
}
