package com.thompson234.bg95.resource;

import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.AirmanSummary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/airman")
@Produces(MediaType.APPLICATION_JSON)
public class AirmanResource {

    private final AirmanDao _airmanDao;

    public AirmanResource(AirmanDao airmanDao) {
        _airmanDao = airmanDao;
    }

    @GET
    public Collection<AirmanSummary> list() {
        return _airmanDao.findAllSummaries();
    }

    @GET
    @Path("/{id}")
    public Airman findById(@PathParam("id") String airmanId) {
        return _airmanDao.findById(airmanId);
    }
}