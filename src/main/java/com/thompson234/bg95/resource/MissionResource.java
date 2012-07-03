package com.thompson234.bg95.resource;

import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Mission;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/mission")
@Produces(MediaType.APPLICATION_JSON)
public class MissionResource {

    private final MissionDao _missionDao;

    public MissionResource(MissionDao missionDao) {
        _missionDao = missionDao;
    }

    @GET
    @Produces("text/x-json")
    public List<Mission> list() {

        return _missionDao.findAll();
    }

    @GET
    @Path("/{id}")
    public Mission findById(@PathParam("id") String missionNumber) {
        return _missionDao.findById(missionNumber);
    }
}