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
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.util.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class SdbAircraftDaoImpl extends AbstractSdbModelDao<Aircraft> implements AircraftDao {

    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String SQUADRON = "squadron";
    private static final String CALLSIGN = "callsign";
    private static final String MODEL = "model";
    private static final String IMAGE_URL = "imageUrl";

    private boolean _cacheLoaded = false;
    private Cache<String, Aircraft> _cache = CacheBuilder.newBuilder().initialCapacity(350).recordStats().build();

    @Inject
    public SdbAircraftDaoImpl(AmazonSimpleDBClient client,
                              @Named("aws.aircraftDomain") String domainName,
                              @Named("aws.forceAircraftDomainReset") boolean forceReset,
                              @Named("dao.aircraftDomainPreCache") boolean preCache) {
        super(client, domainName, forceReset);

        if (preCache) {
            findAll();
        }
    }

    @Override
    protected Aircraft getCachedObject(String id) {
        return _cache.getIfPresent(id);
    }

    @Override
    protected void invalidateCache(String id) {
        _cache.invalidate(id);
    }

    @Override
    protected void cacheObject(Aircraft model) {
        _cache.put(model.getId(), model);
    }

    @Override
    protected List<Aircraft> doFindAll() {

        List<Aircraft> found = null;

        if (_cacheLoaded) {
            found = Lists.newArrayList(_cache.asMap().values());
        } else {
            found = super.doFindAll();
            _cacheLoaded = true;
        }

        return found;
    }

    @Override
    protected List<Aircraft> fromItems(List<Item> items) {

        return Lists.transform(items, new Function<Item, Aircraft>() {
            @Override
            public Aircraft apply(@Nullable Item item) {
                final Aircraft aircraft = new Aircraft();
                aircraft.id(item.getName());

                for (Attribute attribute : item.getAttributes()) {
                    final String name = attribute.getName();
                    final String value = attribute.getValue();

                    if (NUMBER.equals(name)) {
                        aircraft.number(value);
                    } else if (NAME.equals(name)) {
                        aircraft.name(value);
                    } else if (SQUADRON.equals(name)) {
                        aircraft.squadron(value);
                    } else if (CALLSIGN.equals(name)) {
                        aircraft.callsign(value);
                    } else if (MODEL.equals(name)) {
                        aircraft.model(value);
                    } else if (IMAGE_URL.equals(name)) {
                        aircraft.imageUrl(value);
                    }
                }

                return aircraft;
            }
        });
    }

    @Override
    protected List<ReplaceableItem> toReplaceableItems(Aircraft model) {

        final ReplaceableItem item = new ReplaceableItem(model.getId());
        final List<ReplaceableAttribute> attributes = Lists.newArrayList();
        attributes.add(new ReplaceableAttribute(TYPE, Aircraft.class.getName(), true));
        attributes.add(new ReplaceableAttribute(NUMBER, model.getNumber(), true));

        if (!StringUtils.isEmpty(model.getModel())) {
            attributes.add(new ReplaceableAttribute(MODEL, model.getModel(), true));
        }

        attributes.addAll(Utils.toReplaceableAttributes(NAME, model.getNames()));
        attributes.addAll(Utils.toReplaceableAttributes(SQUADRON, model.getSquadrons()));
        attributes.addAll(Utils.toReplaceableAttributes(CALLSIGN, model.getCallsigns()));
        attributes.addAll(Utils.toReplaceableAttributes(IMAGE_URL, model.getImageUrls()));

        item.setAttributes(attributes);
        return Lists.newArrayList(item);
    }

    @Override
    public Aircraft findByNumber(final String number) {

        return findByPredicate(new Predicate<Aircraft>() {
            @Override
            public boolean apply(@Nullable Aircraft input) {
                return StringUtils.equals(input.getNumber(), number);
            }
        });
    }

    @Override
    public Aircraft findByName(final String name) {

        return findByPredicate(new Predicate<Aircraft>() {
            @Override
            public boolean apply(@Nullable Aircraft input) {
                return input.getNames().contains(name);
            }
        });
    }

    @Override
    public List<Aircraft> findAllByNumberLike(final String partialNumber) {

        return findAllByPredicate(new Predicate<Aircraft>() {
            @Override
            public boolean apply(@Nullable Aircraft input) {
                return StringUtils.containsIgnoreCase(input.getNumber(), partialNumber);
            }
        });
    }

    @Override
    public List<Aircraft> findAllByNameLike(final String partialName) {

        return findAllByPredicate(new Predicate<Aircraft>() {
            @Override
            public boolean apply(@Nullable Aircraft input) {

                for (String name : input.getNames()) {
                    if (StringUtils.containsIgnoreCase(name, partialName)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }
}
