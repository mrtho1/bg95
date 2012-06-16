package com.thompson234.bg95;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.guice.Bg95Module;
import com.thompson234.bg95.service.HttpHarvester;
import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class Bg95Configuration extends Configuration {

    @NotEmpty
    @JsonProperty("awsAccessKey")
    private String _awsAccessKey;

    @NotEmpty
    @JsonProperty("awsSecretKey")
    private String _awsSecretKey;

    @NotEmpty
    @JsonProperty("localCacheRoot")
    private String _localCacheRoot;

    @NotEmpty
    @JsonProperty("s3CacheBucket")
    private String _s3CacheBucket;

    @JsonProperty("modelCacheName")
    private String _modelCacheName = "model";

    @JsonProperty("httpCacheName")
    private String _httpCacheName = "data";

    @JsonProperty("cacheNice")
    private long _cacheNice = 10 * 1000;

    @JsonIgnore
    private Injector _injector;

    public Bg95Configuration() {
        _injector = Guice.createInjector(new Bg95Module(this));
    }

    public String getAwsAccessKey() {
        return _awsAccessKey;
    }

    public String getAwsSecretKey() {
        return _awsSecretKey;
    }

    public long getCacheNice() {
        return _cacheNice;
    }

    public String getS3CacheBucket() {
        return _s3CacheBucket;
    }

    public String getLocalCacheRoot() {
        return _localCacheRoot;
    }

    public String getModelCacheName() {
        return _modelCacheName;
    }

    public String getHttpCacheName() {
        return _httpCacheName;
    }

    public AircraftDao getAircraftDao() {
        return _injector.getInstance(AircraftDao.class);
    }

    public AirmanDao getAirmanDao() {
        return _injector.getInstance(AirmanDao.class);
    }

    public MissionDao getMissionDao() {
        return _injector.getInstance(MissionDao.class);
    }

    public HttpHarvester getHttpHarvester() {
        return _injector.getInstance(HttpHarvester.class);
    }
}