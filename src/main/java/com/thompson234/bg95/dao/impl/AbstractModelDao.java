package com.thompson234.bg95.dao.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.thompson234.bg95.dao.ModelDao;
import com.thompson234.bg95.model.Model;
import com.yammer.dropwizard.logging.Log;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractModelDao<T extends Model> implements ModelDao<T> {
    private static final Log _sLog = Log.forClass(AbstractModelDao.class);

    public AbstractModelDao() {
    }

    protected T getCachedObject(String id) {
        return null;
    }

    protected void invalidateCache(String id) {

    }

    protected void cacheObject(T model) {
    }

    protected void onLoad(T model) {
    }

    protected void onSave(T model) {
    }

    protected void onDelete(String key) {
    }

    protected abstract T doFindById(String id);

    protected abstract List<T> doFindAll();

    protected abstract void doSaveAll(Collection<T> all);

    protected abstract void doDeleteAll(Collection<String> ids);

    @Override
    public T findById(String id) {

        T model = getCachedObject(id);

        if (model != null) {
            return model;
        }

        model = doFindById(id);
        onLoad(model);
        cacheObject(model);

        return model;
    }

    @Override
    public List<T> findAll() {

        final List<T> models = doFindAll();
        for (T model : models) {
            onLoad(model);
            cacheObject(model);
        }

        return models;
    }

    @Override
    public List<T> findAllById(List<String> ids) {

        return Lists.transform(ids, new Function<String, T>() {
            @Override
            public T apply(@Nullable String id) {
                return findById(id);
            }
        });
    }

    @Override
    public void save(T model) {
        saveAll(Collections.singleton(model));
    }

    @Override
    public void saveAll(Collection<T> models) {

        doSaveAll(models);
        for (T model : models) {
            onSave(model);
            cacheObject(model);
        }
    }

    @Override
    public void delete(T model) {
        deleteById(model.getId());
    }

    @Override
    public void deleteById(String id) {
        deleteAllById(Collections.singleton(id));
    }

    @Override
    public void deleteAll() {
        deleteAll(findAll());
    }

    @Override
    public void deleteAll(Collection<T> models) {
        deleteAllById(Collections2.transform(models, new Function<T, String>() {
            @Override
            public String apply(@Nullable T model) {
                return model.getId();
            }
        }));
    }

    @Override
    public void deleteAllById(Collection<String> ids) {

        doDeleteAll(ids);
        for (String id : ids) {
            onDelete(id);
            invalidateCache(id);
        }
    }

    protected List<T> findAllByPredicate(Predicate<T> predicate) {
        return Lists.newArrayList(Collections2.filter(findAll(), predicate));
    }

    protected T findByPredicate(Predicate<T> predicate) {
        List<T> all = findAllByPredicate(predicate);

        if (all == null || all.size() != 1) {
            _sLog.warn("Predicate return null or multiple results when looking for single.");
            return null;
        }

        return all.get(0);
    }
}
