package com.thompson234.bg95.dao.impl;

import com.google.common.base.Predicate;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.AircraftSummary;
import com.thompson234.bg95.model.ModelSummary;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AircraftDaoImpl extends AbstractModelDao<Aircraft, AircraftSummary> implements AircraftDao {

    @Inject
    public AircraftDaoImpl(@Named("modelContentManager")ContentManagerRW contentManager,
                           ObjectMapper objectMapper) {
        super(contentManager, objectMapper);
    }

    @Override
    protected Class<Aircraft> getModelClass() {
        return Aircraft.class;
    }

    @Override
    protected AircraftSummary toSummary(Aircraft model) {
        return new AircraftSummary(model);
    }

    @Override
    protected void sort(List<Aircraft> results) {
        Collections.sort(results);
    }

    @Override
    protected void sortSummaries(List<AircraftSummary> results) {
        Collections.sort(results);
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

                for (String name: input.getNames()) {
                    if (StringUtils.containsIgnoreCase(name, partialName)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }
}
