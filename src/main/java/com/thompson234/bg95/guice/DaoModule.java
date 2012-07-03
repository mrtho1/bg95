package com.thompson234.bg95.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.dao.impl.CmAircraftDaoImpl;
import com.thompson234.bg95.dao.impl.CmAirmanDaoImpl;
import com.thompson234.bg95.dao.impl.CmMissionDaoImpl;

import javax.inject.Named;

public class DaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AircraftDao.class).to(CmAircraftDaoImpl.class).in(Singleton.class);
        bind(AirmanDao.class).to(CmAirmanDaoImpl.class).in(Singleton.class);
        bind(MissionDao.class).to(CmMissionDaoImpl.class).in(Singleton.class);
//        bind(AircraftDao.class).to(SdbAircraftDaoImpl.class).in(Singleton.class);
//        bind(AirmanDao.class).to(SdbAirmanDaoImpl.class).in(Singleton.class);
//        bind(MissionDao.class).to(SdbMissionDaoImpl.class).in(Singleton.class);
    }

    public boolean preCache() {
        return true;
    }

    @Provides
    @Named("dao.aircraftDomainPreCache")
    public boolean aircraftDomainPreCache() {
        return preCache();
    }

    @Provides
    @Named("dao.airmanDomainPreCache")
    public boolean airmanDomainPreCache() {
        return preCache();
    }

    @Provides
    @Named("dao.missionDomainPreCache")
    public boolean missionDomainPreCache() {
        return preCache();
    }
}
