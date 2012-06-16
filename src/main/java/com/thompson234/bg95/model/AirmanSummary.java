package com.thompson234.bg95.model;

public class AirmanSummary extends AbstractModelSummary<AirmanSummary> {

    private String _rank;

    public AirmanSummary() {

    }

    public AirmanSummary(Airman airman) {
        super(airman.getId(), airman.getFullName());
        setRank(airman.getRank());
    }

    public String getRank() {
        return _rank;
    }

    public void setRank(String rank) {
        _rank = rank;
    }

    public AirmanSummary rank(String rank) {
        setRank(rank);
        return this;
    }
}
