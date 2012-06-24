package com.thompson234.bg95.dao;

import com.thompson234.bg95.model.Model;

import java.util.Collection;
import java.util.List;

public interface ModelDao<T extends Model> {

    T findById(String id);

    List<T> findAll();

    List<T> findAllById(List<String> ids);

    void save(T model);

    void saveAll(Collection<T> models);

    void delete(T model);

    void deleteById(String id);

    void deleteAll();

    void deleteAll(Collection<T> models);

    void deleteAllById(Collection<String> ids);
}
