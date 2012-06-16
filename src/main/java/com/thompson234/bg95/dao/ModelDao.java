package com.thompson234.bg95.dao;

import com.thompson234.bg95.model.Model;
import com.thompson234.bg95.model.ModelSummary;

import java.util.Collection;
import java.util.List;

public interface ModelDao<T extends Model, S extends ModelSummary> {

    boolean needsData();

    T findById(String id);
    T findBySummary(S summary);
    List<T> findAll();
    List<T> findAllById(Collection<String> ids);
    List<T> findAllBySummary(Collection<S> summaries);

    List<S> findAllSummaries();

    void save(T model);
    void saveAll(Collection<T> models);

    void deleteAll();
    void delete(T model);
    void deleteById(String id);
    void deleteAll(Collection<T> models);
    void deleteAllById(Collection<String> ids);
}
