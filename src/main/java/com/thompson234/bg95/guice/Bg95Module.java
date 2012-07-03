package com.thompson234.bg95.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.thompson234.bg95.Bg95Configuration;
import com.thompson234.bg95.service.HttpHarvester;

public class Bg95Module extends AbstractModule {

    private Bg95Configuration _configuration;

    public Bg95Module(Bg95Configuration configuration) {
        _configuration = configuration;
    }

    @Override
    protected void configure() {

        install(new AWSModule(_configuration.getAwsConfiguration()));
        install(new JacksonModule());
        install(new DaoModule());
        install(new ContentManagerModule(_configuration.getCmConfiguration()));
        install(new SearchModule(_configuration.getSearchConfiguration()));

        bind(HttpHarvester.class).in(Singleton.class);
    }
}
