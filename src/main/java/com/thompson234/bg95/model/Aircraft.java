package com.thompson234.bg95.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;
import java.util.Set;

@JsonAutoDetect
public class Aircraft extends AbstractModel<Aircraft, AircraftSummary> implements Comparable<Aircraft> {

    private String _number;
    private List<String> _names = Lists.newArrayList();
    private Set<String> _squadrons = Sets.newHashSet();
    private Set<String> _callsigns = Sets.newHashSet();
    private String _model;
    private Set<String> _imageUrls = Sets.newHashSet();

    public Aircraft() {
        super(newId());
    }

    public String getNumber() {
        return _number;
    }

    public void setNumber(String number) {
        _number = number;
    }

    public Aircraft number(String number) {
        setNumber(number);
        return this;
    }

    public ImmutableList<String> getNames() {
        return ImmutableList.copyOf(_names);
    }

    public void setNames(List<String> names) {
        _names.clear();
        _names.addAll(names);
    }

    public Aircraft names(List<String> names) {
        setNames(names);
        return this;
    }

    public Aircraft name(String name) {
        _names.add(name);
        return this;
    }

    public ImmutableSet<String> getSquadrons() {
        return ImmutableSet.copyOf(_squadrons);
    }

    public void setSquadrons(Set<String> squadrons) {
        _squadrons.clear();
        _squadrons.addAll(squadrons);
    }

    public Aircraft squadrons(Set<String> squadrons) {
        setSquadrons(squadrons);
        return this;
    }

    public Aircraft squadron(String squadron) {
        _squadrons.add(squadron);
        return this;
    }

    public ImmutableSet<String> getCallsigns() {
        return ImmutableSet.copyOf(_callsigns);
    }

    public void setCallsigns(Set<String> callsigns) {
        _callsigns.clear();
        _callsigns.addAll(callsigns);
    }

    public Aircraft callsigns(Set<String> callsigns) {
        setCallsigns(callsigns);
        return this;
    }

    public Aircraft callsign(String callsign) {
        _callsigns.add(callsign);
        return this;
    }

    public String getModel() {
        return _model;
    }

    public void setModel(String model) {
        _model = model;
    }

    public Aircraft model(String model) {
        setModel(model);
        return this;
    }

    @JsonIgnore
    public boolean isNamed() {
        return !_names.isEmpty();
    }

    public ImmutableSet<String> getImageUrls() {
        return ImmutableSet.copyOf(_imageUrls);
    }

    public void setImageUrls(Set<String> imageUrls) {
        _imageUrls.clear();
        _imageUrls.addAll(imageUrls);
    }

    public Aircraft imageUrls(Set<String> imageUrls) {
        setImageUrls(imageUrls);
        return this;
    }

    public Aircraft imageUrl(String imageUrl) {
        _imageUrls.add(imageUrl);
        return this;
    }

    @JsonIgnore
    public String getPrimaryName() {

        if (!isNamed()) {
            return null;
        }

        return _names.get(0);
    }

    @Override
    protected AircraftSummary createSummary() {
        return new AircraftSummary(this);
    }

    @Override
    public int compareTo(Aircraft rhs) {

        return getNumber().compareTo(rhs.getNumber());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("number", getNumber())
                .add("names", getNames())
                .add("callsigns", getCallsigns())
                .add("squadrons", getSquadrons())
                .add("model", getModel())
                .add("imageUrls", getImageUrls()).toString();
    }

    @Override
    public void sanitize() {
        super.sanitize();
        sanitizeStrings(_names);
        sanitizeStrings(_callsigns);
        sanitizeStrings(_squadrons);
        sanitizeStrings(_imageUrls);
    }
}
