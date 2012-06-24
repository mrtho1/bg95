package com.thompson234.bg95.resource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.model.Aircraft;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("/aircraft")
@Produces(MediaType.APPLICATION_JSON)
public class AircraftResource {

    private final AircraftDao _aircraftDao;

    public AircraftResource(AircraftDao aircraftDao) {
        _aircraftDao = aircraftDao;
    }

    @GET
    public Collection<Map<String, Object>> list() {

        List<Aircraft> all = _aircraftDao.findAll();
        return Lists.transform(_aircraftDao.findAll(), new Function<Aircraft, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(@Nullable Aircraft input) {
                return input.summarize();
            }
        });
    }

    @GET
    @Path("/{id}")
    public Aircraft findById(@PathParam("id") String aircraftId) {
        return _aircraftDao.findById(aircraftId);
    }
}