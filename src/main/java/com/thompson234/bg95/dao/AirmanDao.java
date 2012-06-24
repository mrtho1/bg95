package com.thompson234.bg95.dao;

import com.thompson234.bg95.model.Airman;

import java.util.List;

public interface AirmanDao extends ModelDao<Airman> {

    Airman findByFullName(String fullName);

    List<Airman> findAllByFullNameLike(String partial);
}
