package com.thompson234.bg95.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.MissionSummary;
import com.thompson234.bg95.model.SearchResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;

@Path("/count")
@Produces(MediaType.APPLICATION_JSON)
public class CountResource {

    private final AirmanDao _airmanDao;
    private final AircraftDao _aircraftDao;
    private final MissionDao _missionDao;

    public CountResource(AirmanDao airmanDao, AircraftDao aircraftDao, MissionDao missionDao) {

        _airmanDao = airmanDao;
        _aircraftDao = aircraftDao;
        _missionDao = missionDao;
    }

    @GET
    public Map<String, Integer> count() {
        final ImmutableMap.Builder<String, Integer> builder = ImmutableMap.<String, Integer>builder();

        builder.put("airman", _airmanDao.findAll().size());
        builder.put("aircraft", _aircraftDao.findAll().size());
        builder.put("mission", _missionDao.findAll().size());

        return builder.build();
    }
}