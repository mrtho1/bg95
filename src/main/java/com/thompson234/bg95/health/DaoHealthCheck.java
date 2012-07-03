package com.thompson234.bg95.health;

import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Mission;
import com.yammer.metrics.core.HealthCheck;

import java.util.List;

public class DaoHealthCheck extends HealthCheck {

    private final AirmanDao _airmanDao;
    private final AircraftDao _aircraftDao;
    private final MissionDao _missionDao;

    public DaoHealthCheck(AirmanDao airmanDao, AircraftDao aircraftDao, MissionDao missionDao) {
        super("dao");

        _airmanDao = airmanDao;
        _aircraftDao = aircraftDao;
        _missionDao = missionDao;
    }

    @Override
    protected Result check() throws Exception {

        final List<Airman> airmen = _airmanDao.findAll();
        final List<Aircraft> aircraft = _aircraftDao.findAll();
        final List<Mission> missions = _missionDao.findAll();

        if (airmen == null || airmen.isEmpty()) {
            return Result.unhealthy("No airmen");
        } else if (aircraft == null || aircraft.isEmpty()) {
            return Result.unhealthy("No aircraft");
        } else if (missions == null || missions.isEmpty()) {
            return Result.unhealthy("No missions");
        }

        return Result.healthy(String.format("%d Airmen; %d Aircraft; %d Missions", airmen.size(), aircraft.size(), missions.size()));
    }
}