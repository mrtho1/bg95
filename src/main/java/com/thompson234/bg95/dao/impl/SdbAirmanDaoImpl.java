package com.thompson234.bg95.dao.impl;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Name;
import com.thompson234.bg95.util.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class SdbAirmanDaoImpl extends AbstractSdbModelDao<Airman> implements AirmanDao {

    private static final String FULL_NAME = "fullName";
    private static final String RANK = "rank";
    private static final String ROLE = "role";
    private static final String NOTE = "note";
    private static final String UNIT = "unit";
    private static final String IMAGE_URL = "imageUrl";

    private boolean _cacheLoaded = false;
    private Cache<String, Airman> _cache = CacheBuilder.newBuilder().initialCapacity(8000).recordStats().build();

    @Inject
    public SdbAirmanDaoImpl(AmazonSimpleDBClient client,
                            @Named("domain.airman.name") String domainName,
                            @Named("domain.airman.forceReset") boolean forceReset,
                            @Named("domain.airman.preCache") boolean preCache) {
        super(client, domainName, forceReset);

        if (preCache) {
            findAll();
        }
    }

    @Override
    protected Airman getCachedObject(String id) {
        return _cache.getIfPresent(id);
    }

    @Override
    protected void invalidateCache(String id) {
        _cache.invalidate(id);
    }

    @Override
    protected void cacheObject(Airman model) {
        _cache.put(model.getId(), model);
    }

    @Override
    protected List<Airman> doFindAll() {

        List<Airman> found = null;

        if (_cacheLoaded) {
            found = Lists.newArrayList(_cache.asMap().values());
        } else {
            found = super.doFindAll();
            _cacheLoaded = true;
        }

        return found;
    }

    @Override
    protected List<Airman> fromItems(List<Item> items) {

        return Lists.transform(items, new Function<Item, Airman>() {
            @Override
            public Airman apply(@Nullable Item item) {
                final Airman airman = new Airman();
                airman.id(item.getName());

                for (Attribute attribute : item.getAttributes()) {
                    final String name = attribute.getName();
                    final String value = attribute.getValue();

                    if (FULL_NAME.equals(name)) {
                        airman.fullName(value);
                    } else if (RANK.equals(name)) {
                        airman.rank(value);
                    } else if (ROLE.equals(name)) {
                        airman.role(value);
                    } else if (NOTE.equals(name)) {
                        airman.note(value);
                    } else if (UNIT.equals(name)) {
                        airman.unit(value);
                    } else if (IMAGE_URL.equals(name)) {
                        airman.imageUrl(value);
                    }
                }

                return airman;
            }
        });
    }

    @Override
    protected List<ReplaceableItem> toReplaceableItems(Airman model) {

        final ReplaceableItem item = new ReplaceableItem(model.getId());
        final List<ReplaceableAttribute> attributes = Lists.newArrayList();
        attributes.add(new ReplaceableAttribute(TYPE, Airman.class.getName(), true));
        attributes.add(new ReplaceableAttribute(FULL_NAME, model.getFullName(), true));
        attributes.addAll(Utils.toReplaceableAttributes(RANK, model.getRanks()));
        attributes.addAll(Utils.toReplaceableAttributes(ROLE, model.getRoles()));
        attributes.addAll(Utils.toReplaceableAttributes(NOTE, model.getNotes()));
        attributes.addAll(Utils.toReplaceableAttributes(UNIT, model.getUnits()));
        attributes.addAll(Utils.toReplaceableAttributes(IMAGE_URL, model.getImageUrls()));

        item.setAttributes(attributes);
        return Lists.newArrayList(item);
    }

    @Override
    public Airman findByFullName(String fullName) {

        final Name name = new Name(fullName);
        return findByPredicate(new Predicate<Airman>() {
            @Override
            public boolean apply(@Nullable Airman input) {
                return name.compareTo(input.getName()) == 0;
            }
        });
    }

    @Override
    public List<Airman> findAllByFullNameLike(final String partial) {

        return findAllByPredicate(new Predicate<Airman>() {
            @Override
            public boolean apply(@Nullable Airman input) {

                return StringUtils.containsIgnoreCase(Utils.removePunctuation(input.getFullName()),
                        Utils.removePunctuation(partial));
            }
        });
    }
}
