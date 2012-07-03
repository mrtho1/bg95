package com.thompson234.bg95.resource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.model.Airman;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("/airman")
@Produces(MediaType.APPLICATION_JSON)
public class AirmanResource {

    private final AirmanDao _airmanDao;

    public AirmanResource(AirmanDao airmanDao) {
        _airmanDao = airmanDao;
    }

    @GET
    @Produces("text/x-json")
    public List<Airman> list() {

        return _airmanDao.findAll();
    }

    @GET
    @Path("/{id}")
    public Airman findById(@PathParam("id") String airmanId) {
        return _airmanDao.findById(airmanId);
    }
}