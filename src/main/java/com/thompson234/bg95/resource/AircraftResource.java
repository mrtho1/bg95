package com.thompson234.bg95.resource;

import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.model.Aircraft;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/aircraft")
@Produces(MediaType.APPLICATION_JSON)
public class AircraftResource {

    private final AircraftDao _aircraftDao;

    public AircraftResource(AircraftDao aircraftDao) {
        _aircraftDao = aircraftDao;
    }

    @GET
    @Produces("text/x-json")
    public List<Aircraft> list() {

        return _aircraftDao.findAll();
    }

    @GET
    @Path("/{id}")
    public Aircraft findById(@PathParam("id") String aircraftId) {
        return _aircraftDao.findById(aircraftId);
    }
}