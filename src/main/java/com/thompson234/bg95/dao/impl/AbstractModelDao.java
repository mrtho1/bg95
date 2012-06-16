package com.thompson234.bg95.dao.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.dao.ModelDao;
import com.thompson234.bg95.model.Model;
import com.thompson234.bg95.model.ModelSummary;
import com.yammer.dropwizard.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractModelDao<T extends Model, S extends ModelSummary> implements ModelDao<T, S> {
    private static final Log _sLog = Log.forClass(AbstractModelDao.class);

    private ContentManagerRW _contentManager;
    private ObjectMapper _objectMapper;

    private Cache<String, T> _objectCache = CacheBuilder.newBuilder().build();

    public AbstractModelDao(ContentManagerRW contentManager,
                            ObjectMapper mapper) {

        _contentManager = contentManager;
        _objectMapper = mapper;
        init();
    }

    protected abstract Class<T> getModelClass();

    @PostConstruct
    public void init() {
        loadModels();
    }

    protected String getModelKey() {
        final String simpleName = getModelClass().getSimpleName().toLowerCase();
        return simpleName + ".json";
    }

    @Override
    public boolean needsData() {
        return _objectCache.size() == 0;
    }

    @Override
    public T findById(String id) {
        return _objectCache.getIfPresent(id);
    }

    @Override
    public T findBySummary(S summary) {
        return findById(summary.getId());
    }

    @Override
    public List<T> findAll() {
        final List<T> results = Lists.newArrayList(_objectCache.asMap().values());
        sort(results);

        return results;
    }

    @Override
    public List<T> findAllById(Collection<String> ids) {
        return findAllById(ids, true);
    }

    protected List<T> findAllById(Collection<String> ids, boolean failOnNotFound) {
        List<T> results = Lists.newArrayList();

        for (String id: ids) {
            final T model = findById(id);

            if (model != null) {
                results.add(model);
            } else if (failOnNotFound) {
                throw new IllegalArgumentException(id);
            }
        }

        sort(results);
        return results;
    }

    @Override
    public List<T> findAllBySummary(Collection<S> summaries) {

        List<String> ids = Lists.newArrayList();
        for (S summary: summaries) {
            ids.add(summary.getId());
        }

        return findAllById(ids);
    }

    @Override
    public List<S> findAllSummaries() {
        final List<T> all = findAll();
        final List<S> summaries = Lists.newArrayList();

        for (T model: all) {
            summaries.add(toSummary(model));
        }

        sortSummaries(summaries);
        return summaries;
    }

    protected abstract S toSummary(T model);

    protected void updateCache(T model) {
        _objectCache.put(model.getId(), model);
        onSave(model);
    }

    @Override
    public void save(T model) {
        model.sanitize();
        updateCache(model);
        storeModels();
    }

    @Override
    public void saveAll(Collection<T> models) {
        for (T model: models) {
            model.sanitize();
            updateCache(model);
        }

        storeModels();
    }

    @Override
    public void deleteById(String id) {
        final T model = _objectCache.getIfPresent(id);
        _objectCache.invalidate(id);

        if (model != null) {
            onDelete(model);
            storeModels();
        }
    }

    @Override
    public void delete(T model) {
        deleteById(model.getId());
    }

    @Override
    public void deleteAll() {
        deleteAll(findAll());
    }

    @Override
    public void deleteAll(Collection<T> models) {

        for (T model: models) {
            _objectCache.invalidate(model.getId());
            onDelete(model);
        }

        storeModels();
    }

    @Override
    public void deleteAllById(Collection<String> ids) {

        final Collection<T> toDelete = findAllById(ids, false);
        deleteAll(toDelete);
    }

    protected void storeModels() {
        try {
            final byte[] jsonContent = _objectMapper.writeValueAsBytes(_objectCache.asMap().values());
            final Map<String, String> meta = Maps.newHashMap();
            meta.put(ContentManagerRW.META_CONTENT_TYPE, "application/json");
            meta.put(ContentManagerRW.META_LENGTH, "" + jsonContent.length);
            meta.put(ContentManagerRW.META_ORIGINAL_KEY, getModelKey());

            _contentManager.store(getModelKey(), new ByteArrayInputStream(jsonContent), meta);
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private CollectionType createCollectionType() {
        return _objectMapper.getTypeFactory().constructCollectionType(List.class, getModelClass());
    }

    protected void loadModels() {

        try {
            final InputStream modelIn = _contentManager.load(getModelKey());

            if (modelIn == null) {
                _sLog.debug("No model content for {}", getModelClass());
                return;
            }

            final List<T> models =
                _objectMapper.readValue(_contentManager.load(getModelKey()), createCollectionType());
            _objectCache.invalidateAll();

            for (T model: models) {
                _objectCache.put(model.getId(), model);
                onLoad(model);
            }
        } catch (Exception ex) {
            _sLog.debug(ex, "Error loading models from json: {}", ex.getMessage());
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

    protected void onLoad(T model) {
    }

    protected void onSave(T model) {
    }

    protected void onDelete(T model) {
    }

    protected void sort(List<T> results) {
    }

    protected void sortSummaries(List<S> results) {
    }
}
