package com.thompson234.bg95.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class AircraftSummary extends AbstractModelSummary<AircraftSummary> {

    private List<String> _names = Lists.newArrayList();

    public AircraftSummary() {

    }

    public AircraftSummary(Aircraft ac) {
        super(ac.getId(), ac.getNumber());
        setNames(ac.getNames());
    }

    public ImmutableList<String> getNames() {
        return ImmutableList.copyOf(_names);
    }

    public void setNames(List<String> names) {
        _names.clear();
        _names.addAll(names);
    }

    public AircraftSummary names(List<String> names) {
        setNames(names);
        return this;
    }

    public AircraftSummary name(String name) {
        _names.add(name);
        return this;
    }
}
