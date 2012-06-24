package com.thompson234.bg95.resource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Mission;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;

@Path("/mission")
@Produces(MediaType.APPLICATION_JSON)
public class MissionResource {

    private final MissionDao _missionDao;

    public MissionResource(MissionDao missionDao) {
        _missionDao = missionDao;
    }

    @GET
    public Collection<Map<String, Object>> list() {

        return Lists.transform(_missionDao.findAll(), new Function<Mission, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(@Nullable Mission input) {
                return input.summarize();
            }
        });
    }

    @GET
    @Path("/{id}")
    public Mission findById(@PathParam("id") String missionNumber) {
        return _missionDao.findById(missionNumber);
    }
}