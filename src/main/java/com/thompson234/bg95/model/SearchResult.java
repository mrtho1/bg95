package com.thompson234.bg95.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class SearchResult {

    private String _query;

    private List<String> _airmenIds = Lists.newArrayList();
    private List<String> _aircraftIds = Lists.newArrayList();
    private List<String> _missionIds = Lists.newArrayList();
    
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

    public ImmutableList<String> getAirmenIds() {
        return ImmutableList.copyOf(_airmenIds);
    }

    public void setAirmenIds(Collection<String> airmenIds) {
        _airmenIds.clear();
        _airmenIds.addAll(airmenIds);
    }

    public SearchResult airmenIds(Collection<String> airmenIds) {
        setAirmenIds(airmenIds);
        return this;
    }

    public SearchResult airmanId(String airmanId) {
        _airmenIds.add(airmanId);
        return this;
    }

    public ImmutableList<String> getAircraftIds() {
        return ImmutableList.copyOf(_aircraftIds);
    }

    public void setAircraftIds(Collection<String> aircraftIds) {
        _aircraftIds.clear();
        _aircraftIds.addAll(aircraftIds);
    }

    public SearchResult aircraftIds(Collection<String> aircraftIds) {
        setAircraftIds(aircraftIds);
        return this;
    }

    public SearchResult aircraftId(String aircraft) {
        _aircraftIds.add(aircraft);
        return this;
    }

    public ImmutableList<String> getMissionIds() {
        return ImmutableList.copyOf(_missionIds);
    }

    public void setMissionIds(Collection<String> missions) {
        _missionIds.clear();
        _missionIds.addAll(missions);
    }

    public SearchResult missionIds(Collection<String> missions) {
        setMissionIds(missions);
        return this;
    }

    public SearchResult missionId(String mission) {
        _missionIds.add(mission);
        return this;
    }
}
