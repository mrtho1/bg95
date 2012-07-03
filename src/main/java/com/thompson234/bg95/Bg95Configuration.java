package com.thompson234.bg95;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.guice.Bg95Module;
import com.thompson234.bg95.service.HttpHarvester;
import com.thompson234.bg95.service.SearchService;
import com.yammer.dropwizard.config.Configuration;
import org.apache.lucene.store.Directory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Bg95Configuration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("aws")
    protected AWSConfiguration _awsConfiguration = new AWSConfiguration();

    @Valid
    @NotNull
    @JsonProperty("contentManager")
    protected ContentManagerConfiguration _cmConfiguration = new ContentManagerConfiguration();

    @Valid
    @NotNull
    @JsonProperty("search")
    protected SearchConfiguration _searchConfiguration = new SearchConfiguration();

    @JsonIgnore
    private Injector _injector;

    public Bg95Configuration() {
    }

    protected synchronized Injector getInjector() {

        if (_injector == null) {
            _injector = Guice.createInjector(new Bg95Module(this));
        }

        return _injector;
    }

    public AWSConfiguration getAwsConfiguration() {
        return _awsConfiguration;
    }

    public ContentManagerConfiguration getCmConfiguration() {
        return _cmConfiguration;
    }

    public SearchConfiguration getSearchConfiguration() {
        return _searchConfiguration;
    }

    public AircraftDao getAircraftDao() {
        return getInjector().getInstance(AircraftDao.class);
    }

    public AirmanDao getAirmanDao() {
        return getInjector().getInstance(AirmanDao.class);
    }

    public MissionDao getMissionDao() {
        return getInjector().getInstance(MissionDao.class);
    }

    public HttpHarvester getHttpHarvester() {
        return getInjector().getInstance(HttpHarvester.class);
    }

    public SearchService getSearchService() {
        return getInjector().getInstance(SearchService.class);
    }

    public Directory getDirectory() {
        return getInjector().getInstance(Directory.class);
    }
}
