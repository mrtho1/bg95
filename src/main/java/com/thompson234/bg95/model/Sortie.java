package com.thompson234.bg95.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collection;
import java.util.Set;

@JsonAutoDetect
public class Sortie {

    private Aircraft _aircraft;
    private Set<CrewAssignment> _crewAssignments = Sets.newHashSet();

    public Sortie() {

    }

    public Aircraft getAircraft() {
        return _aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        _aircraft = aircraft;
    }

    public Sortie aircraft(Aircraft aircraft) {
        setAircraft(aircraft);
        return this;
    }

    public ImmutableSet<CrewAssignment> getCrewAssignments() {
        return ImmutableSet.copyOf(_crewAssignments);
    }

    public void setCrewAssignments(Collection<CrewAssignment> crewAssignments) {
        _crewAssignments.clear();
        _crewAssignments.addAll(crewAssignments);
    }

    public Sortie crewAssignments(Collection<CrewAssignment> crewAssignments) {
        setCrewAssignments(crewAssignments);
        return this;
    }

    public Sortie crewAssignment(CrewAssignment crewAssignment) {
        _crewAssignments.add(crewAssignment);
        return this;
    }

    public Airman getCrewByRole(String role) {

        for (CrewAssignment crewAssignment : getCrewAssignments()) {
            if (crewAssignment.getRole().equalsIgnoreCase(role)) {
                return crewAssignment.getAirman();
            }
        }

        return null;
    }

    public CrewAssignment getCrewAssignmentByAirmanId(String airmanId) {
        for (CrewAssignment ca : getCrewAssignments()) {

            if (ca.getAirman().getId().equals(airmanId)) {
                return ca;
            }
        }

        return null;
    }

    @JsonIgnore
    public ImmutableSet<Airman> getFlightCrew() {

        final ImmutableSet.Builder<Airman> builder = ImmutableSet.<Airman>builder();
        for (CrewAssignment crewAssignment : getCrewAssignments()) {
            builder.add(crewAssignment.getAirman());
        }

        return builder.build();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("aircraft", getAircraft())
                .add("crewAssignments", getCrewAssignments()).toString();
    }
}
