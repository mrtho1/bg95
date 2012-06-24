package com.thompson234.bg95.resource;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.SearchResult;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private final AirmanDao _airmanDao;
    private final AircraftDao _aircraftDao;
    private final MissionDao _missionDao;

    public SearchResource(AirmanDao airmanDao, AircraftDao aircraftDao, MissionDao missionDao) {

        _airmanDao = airmanDao;
        _aircraftDao = aircraftDao;
        _missionDao = missionDao;
    }

    @GET
    public SearchResult search(@QueryParam("q") String query) {

        if (Strings.isNullOrEmpty(query)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        if (query.length() < 3) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }

        final List<Airman> airmen = _airmanDao.findAllByFullNameLike(query);

        final Set<Aircraft> aircraft = Sets.newHashSet();
        aircraft.addAll(_aircraftDao.findAllByNameLike(query));
        aircraft.addAll(_aircraftDao.findAllByNumberLike(query));

        final Set<Mission> missions = Sets.newHashSet();
        missions.addAll(_missionDao.findAllByDestinationLike(query));
        for (Airman airman : airmen) {
            missions.addAll(_missionDao.findAllByAirman(airman));
        }

        final List<Aircraft> aircraftResult = Lists.newArrayList(aircraft);
        Collections.sort(aircraftResult);

        final List<Mission> missionResult = Lists.newArrayList(missions);
        Collections.sort(missionResult);

        final List<Map<String, Object>> airmanSummaries = Lists.transform(airmen, new Function<Airman, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(@Nullable Airman input) {
                return input.summarize();
            }
        });

        final List<Map<String, Object>> aircraftSummaries = Lists.transform(aircraftResult, new Function<Aircraft, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(@Nullable Aircraft input) {
                return input.summarize();
            }
        });

        final List<Map<String, Object>> missionSummaries = Lists.transform(missionResult, new Function<Mission, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(@Nullable Mission input) {
                return input.summarize();
            }
        });

        return new SearchResult(query).airmen(airmanSummaries).aircraft(aircraftSummaries).missions(missionSummaries);
    }
}