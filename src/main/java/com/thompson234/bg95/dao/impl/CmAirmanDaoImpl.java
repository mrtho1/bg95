package com.thompson234.bg95.dao.impl;

import com.google.common.base.Predicate;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Name;
import com.thompson234.bg95.util.Utils;
import com.yammer.dropwizard.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class CmAirmanDaoImpl extends AbstractCmModelDao<Airman> implements AirmanDao {
    private static final Log _sLog = Log.forClass(CmAirmanDaoImpl.class);

    @Inject
    public CmAirmanDaoImpl(@Named("modelContentManager") ContentManagerRW contentManager,
                           ObjectMapper objectMapper) {
        super(contentManager, objectMapper);
    }

    @Override
    protected Class<Airman> getModelClass() {
        return Airman.class;
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
