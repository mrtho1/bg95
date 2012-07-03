package com.thompson234.bg95.model;

import com.google.common.base.Objects;
import com.thompson234.bg95.json.Views;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonView;

@JsonAutoDetect
public class CrewAssignment {

    private String _airmanId;
    private Airman _airman;
    private String _role;
    private String _status;

    public CrewAssignment() {

    }

    @JsonView(value = {Views.Storage.class})
    public String getAirmanId() {
        return (_airman != null) ? _airman.getId() : _airmanId;
    }

    public void setAirmanId(String airmanId) {
        _airmanId = airmanId;
    }

    @JsonView(value = {Views.Detail.class})
    public Airman getAirman() {
        return _airman;
    }

    public void setAirman(Airman airman) {
        _airman = airman;
    }

    public CrewAssignment airman(Airman airman) {
        setAirman(airman);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public String getRole() {
        return _role;
    }

    public void setRole(String role) {
        _role = role;
    }

    public CrewAssignment role(String role) {
        setRole(role);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public CrewAssignment status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("airman", getAirman())
                .add("role", getRole())
                .add("status", getStatus()).toString();
    }
}
