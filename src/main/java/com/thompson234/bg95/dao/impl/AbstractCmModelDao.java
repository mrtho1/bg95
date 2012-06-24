package com.thompson234.bg95.dao.impl;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.model.Model;
import com.yammer.dropwizard.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractCmModelDao<T extends Model> extends AbstractModelDao<T> {
    private static final Log _sLog = Log.forClass(AbstractCmModelDao.class);

    private ContentManagerRW _contentManager;
    private ObjectMapper _objectMapper;

    private Cache<String, T> _objectCache = CacheBuilder.newBuilder().build();

    public AbstractCmModelDao(ContentManagerRW contentManager,
                              ObjectMapper mapper) {

        _contentManager = contentManager;
        _objectMapper = mapper;
        init();
    }

    @PostConstruct
    public void init() {
        loadModels();
    }

    protected abstract Class<T> getModelClass();

    protected String getModelKey() {
        final String simpleName = getModelClass().getSimpleName().toLowerCase();
        return simpleName + ".json";
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

            for (T model : models) {
                cacheObject(model);
            }
        } catch (Exception ex) {
            _sLog.debug(ex, "Error loading models from json: {}", ex.getMessage());
        }
    }

    protected void storeModels() {
        try {
            final byte[] jsonContent = _objectMapper.writeValueAsBytes(findAll());
            final Map<String, String> meta = Maps.newHashMap();
            meta.put(ContentManagerRW.META_CONTENT_TYPE, "application/json");
            meta.put(ContentManagerRW.META_LENGTH, "" + jsonContent.length);
            meta.put(ContentManagerRW.META_ORIGINAL_KEY, getModelKey());

            _contentManager.store(getModelKey(), new ByteArrayInputStream(jsonContent), meta);
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    protected T getCachedObject(String id) {
        return _objectCache.getIfPresent(id);
    }

    @Override
    protected void invalidateCache(String id) {
        _objectCache.invalidate(id);
    }

    @Override
    protected void cacheObject(T model) {
        _objectCache.put(model.getId(), model);
    }

    @Override
    protected T doFindById(String id) {
        return _objectCache.getIfPresent(id);
    }

    @Override
    protected List<T> doFindAll() {
        return Lists.newArrayList(_objectCache.asMap().values());
    }

    @Override
    protected void doSaveAll(Collection<T> all) {
        for (T model : all) {
            cacheObject(model);
        }

        storeModels();
    }

    @Override
    protected void doDeleteAll(Collection<String> ids) {
        for (String id : ids) {
            invalidateCache(id);
        }

        storeModels();
    }
}
