package com.thompson234.bg95.resource;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.SearchResult;
import com.thompson234.bg95.service.SearchService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private final SearchService _searchService;
    private final AircraftDao _aircraftDao;
    private final AirmanDao _airmanDao;
    private final MissionDao _missionDao;

    public SearchResource(SearchService searchService,
                          AircraftDao aircraftDao,
                          AirmanDao airmanDao,
                          MissionDao missionDao) {

        _searchService = searchService;
        _aircraftDao = aircraftDao;
        _airmanDao = airmanDao;
        _missionDao = missionDao;
    }

    @GET
    @Produces("text/x-json")
    public Map<String, Object> search(@QueryParam("q") String query) {

        if (Strings.isNullOrEmpty(query)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        if (query.length() < 3) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }

        final SearchResult searchResult = _searchService.search(query);
        final List<Airman> airmen = _airmanDao.findAllById(searchResult.getAirmenIds());
        final List<Aircraft> aircraft = _aircraftDao.findAllById(searchResult.getAircraftIds());
        final List<Mission> missions = _missionDao.findAllById(searchResult.getMissionIds());

        final Map<String, Object> result = Maps.newHashMap();
        result.put("query", query);
        result.put("airmen", airmen);
        result.put("aircraft", aircraft);
        result.put("missions", missions);

        return result;
    }

    @POST
    @Path("/build-index")
    public void buildIndex() {
        _searchService.buildIndex();
    }
}