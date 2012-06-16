package com.thompson234.bg95.dao;

import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.AircraftSummary;

import java.util.List;

public interface AircraftDao extends ModelDao<Aircraft, AircraftSummary> {

    public Aircraft findByNumber(String number);
    public Aircraft findByName(String name);
    public List<Aircraft> findAllByNumberLike(String partialNumber);
    public List<Aircraft> findAllByNameLike(String partialName);
}
