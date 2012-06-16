package com.thompson234.bg95.model;

import java.util.Date;

public class MissionSummary extends AbstractModelSummary<MissionSummary> {

    private int _number;
    private Date _date;
    private int _tookOff;
    private int _completed;
    private int _damaged;
    private int _lost;
    private int _salvaged;
    private int _sortieCount;

    public MissionSummary() {

    }

    public MissionSummary(Mission mission) {
        super(mission.getId(), mission.getDestination());
        number(mission.getNumber())
                .date(mission.getDate())
                .tookOff(mission.getTookOff())
                .completed(mission.getCompleted())
                .damaged(mission.getDamaged())
                .lost(mission.getLost())
                .salvaged(mission.getSalvaged())
                .sortieCount(mission.getSorties().size());
    }

    public int getNumber() {
        return _number;
    }

    public void setNumber(int number) {
        _number = number;
    }

    public MissionSummary number(int number) {
        setNumber(number);
        return this;
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    public MissionSummary date(Date date) {
        setDate(date);
        return this;
    }

    public int getTookOff() {
        return _tookOff;
    }

    public void setTookOff(int tookOff) {
        _tookOff = tookOff;
    }

    public MissionSummary tookOff(int tookOff) {
        setTookOff(tookOff);
        return this;
    }

    public int getCompleted() {
        return _completed;
    }

    public void setCompleted(int completed) {
        _completed = completed;
    }

    public MissionSummary completed(int completed) {
        setCompleted(completed);
        return this;
    }

    public int getDamaged() {
        return _damaged;
    }

    public void setDamaged(int damaged) {
        _damaged = damaged;
    }

    public MissionSummary damaged(int damaged) {
        setDamaged(damaged);
        return this;
    }

    public int getLost() {
        return _lost;
    }

    public void setLost(int lost) {
        _lost = lost;
    }

    public MissionSummary lost(int lost) {
        setLost(lost);
        return this;
    }

    public int getSalvaged() {
        return _salvaged;
    }

    public void setSalvaged(int salvaged) {
        _salvaged = salvaged;
    }

    public MissionSummary salvaged(int salvaged) {
        setSalvaged(salvaged);
        return this;
    }

    public int getSortieCount() {
        return _sortieCount;
    }

    public void setSortieCount(int sortieCount) {
        _sortieCount = sortieCount;
    }

    public MissionSummary sortieCount(int sortieCount) {
        setSortieCount(sortieCount);
        return this;
    }
}
