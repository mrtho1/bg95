package com.thompson234.bg95.model;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.thompson234.bg95.json.Views;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.Date;
import java.util.Set;

@JsonAutoDetect
public class Mission extends AbstractModel<Mission> implements Comparable<Mission> {

    private static final char TOOK_OFF = 'T';
    private static final char COMPLETED = 'C';
    private static final char DAMAGED = 'D';
    private static final char LOST = 'L';
    private static final char SALVAGED = 'S';

    private int _number;
    private Date _date;
    private String _destination;
    private int _tookOff;
    private int _completed;
    private int _damaged;
    private int _lost;
    private int _salvaged;

    private Set<Sortie> _sorties = Sets.newHashSet();

    public Mission() {
        super(newId());
    }

    @JsonView(value = {Views.Summary.class})
    public String getLabel() {
        return getDestination();
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public int getNumber() {
        return _number;
    }

    public void setNumber(int number) {
        _number = number;
    }

    public Mission number(int number) {
        setNumber(number);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    public Mission date(Date date) {
        setDate(date);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public String getDestination() {
        return _destination;
    }

    public void setDestination(String destination) {
        _destination = destination;
    }

    public Mission destination(String destination) {
        setDestination(destination);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public int getTookOff() {
        return _tookOff;
    }

    public void setTookOff(int tookOff) {
        _tookOff = tookOff;
    }

    public Mission tookOff(int tookOff) {
        setTookOff(tookOff);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public int getCompleted() {
        return _completed;
    }

    public void setCompleted(int completed) {
        _completed = completed;
    }

    public Mission completed(int completed) {
        setCompleted(completed);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public int getDamaged() {
        return _damaged;
    }

    public void setDamaged(int damaged) {
        _damaged = damaged;
    }

    public Mission damaged(int damaged) {
        setDamaged(damaged);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public int getLost() {
        return _lost;
    }

    public void setLost(int lost) {
        _lost = lost;
    }

    public Mission lost(int lost) {
        setLost(lost);
        return this;
    }

    @JsonView(value = {Views.Summary.class, Views.Storage.class, Views.Detail.class})
    public int getSalvaged() {
        return _salvaged;
    }

    public void setSalvaged(int salvaged) {
        _salvaged = salvaged;
    }

    public Mission salvaged(int salvaged) {
        setSalvaged(salvaged);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public ImmutableSet<Sortie> getSorties() {
        return ImmutableSet.copyOf(_sorties);
    }

    public void setSorties(Set<Sortie> sorties) {
        _sorties.clear();
        _sorties.addAll(sorties);
    }

    public Mission sorties(Set<Sortie> sorties) {
        setSorties(sorties);
        return this;
    }

    public Mission sortie(Sortie sortie) {
        _sorties.add(sortie);
        return this;
    }

    @JsonView(value = {Views.Summary.class})
    public int getSortieCount() {
        return _sorties.size();
    }

    public Sortie getSortieByAirmanId(String airmanId) {

        for (Sortie sortie : getSorties()) {

            for (CrewAssignment ca: sortie.getCrewAssignments()) {

                if (ca.getAirman().getId().equals(airmanId)) {
                    return sortie;
                }
            }
        }

        return null;
    }

    public Sortie getSortieByAircraftNumber(String aircraftNumber) {

        for (Sortie sortie : getSorties()) {

            if (sortie.getAircraft().getNumber().equals(aircraftNumber)) {
                return sortie;
            }
        }

        return null;
    }

    public Sortie getSortieByAircraftId(String aircraftId) {

        for (Sortie sortie : getSorties()) {

            if (sortie.getAircraft().getId().equals(aircraftId)) {
                return sortie;
            }
        }

        return null;
    }

    public Mission stats(String stats) {
        if (!Strings.isNullOrEmpty(stats)) {
            String[] tokens = stats.split(" ");

            for (String token : tokens) {
                if (token.length() < 2) {
                    throw new IllegalArgumentException(stats);
                }

                int value = -1;

                try {
                    value = Integer.parseInt(token.substring(0, token.length() - 1));
                } catch (NumberFormatException ex) {
                    throw Throwables.propagate(ex);
                }
                char designator = token.charAt(token.length() - 1);

                switch (designator) {
                    case TOOK_OFF:
                        tookOff(value);
                        break;
                    case COMPLETED:
                        completed(value);
                        break;
                    case DAMAGED:
                        damaged(value);
                        break;
                    case LOST:
                        lost(value);
                        break;
                    case SALVAGED:
                        salvaged(value);
                        break;
                    default:
                        throw new IllegalArgumentException(stats);
                }
            }
        }

        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("number", getNumber())
                .add("date", getDate())
                .add("destination", getDestination())
                .add("tookOff", getTookOff())
                .add("completed", getCompleted())
                .add("damaged", getDamaged())
                .add("lost", getLost())
                .add("salvaged", getSalvaged())
                .add("sorties", getSorties()).toString();
    }

    @Override
    public int compareTo(Mission rhs) {

        if (getNumber() < rhs.getNumber()) {
            return -1;
        } else if (getNumber() == rhs.getNumber()) {
            return 0;
        } else {
            return 1;
        }
    }
}
