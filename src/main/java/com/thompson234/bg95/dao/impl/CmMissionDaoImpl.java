package com.thompson234.bg95.dao.impl;

import com.google.common.base.Predicate;
import com.thompson234.bg95.content.ContentManagerRW;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.CrewAssignment;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.Sortie;
import com.yammer.dropwizard.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class CmMissionDaoImpl extends AbstractCmModelDao<Mission> implements MissionDao {
    private static final Log _sLog = Log.forClass(CmMissionDaoImpl.class);

    private final AirmanDao _airmanDao;
    private final AircraftDao _aircraftDao;

    @Inject
    public CmMissionDaoImpl(@Named("contentManager.modelContentManager") ContentManagerRW contentManager,
                            ObjectMapper objectMapper,
                            AirmanDao airmanDao,
                            AircraftDao aircraftDao) {

        super(contentManager, objectMapper);
        _airmanDao = airmanDao;
        _aircraftDao = aircraftDao;

        //should already be loaded.
        final List<Mission> all = findAll();
        for (Mission mission: all) {
            for (Sortie sortie: mission.getSorties()) {
                final Aircraft aircraft = _aircraftDao.findById(sortie.getAircraftId());
                sortie.setAircraft(aircraft);

                for (CrewAssignment ca: sortie.getCrewAssignments()) {
                    final Airman airman = _airmanDao.findById(ca.getAirmanId());
                    ca.setAirman(airman);
                }
            }
        }
    }

    protected Class<Mission> getModelClass() {
        return Mission.class;
    }

    @Override
    protected String getModelKey() {
        return "missions.json";
    }

    @Override
    public List<Mission> findAllByAirman(final Airman airman) {

        return findAllByPredicate(new Predicate<Mission>() {
            @Override
            public boolean apply(@Nullable Mission input) {
                for (Sortie sortie : input.getSorties()) {

                    for (CrewAssignment ca : sortie.getCrewAssignments()) {

                        if (ca.getAirman().equals(airman)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        });
    }

    @Override
    public List<Mission> findAllByAircraft(final Aircraft aircraft) {

        return findAllByPredicate(new Predicate<Mission>() {
            @Override
            public boolean apply(@Nullable Mission input) {
                for (Sortie sortie : input.getSorties()) {
                    if (sortie.getAircraft().equals(aircraft)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public List<Mission> findAllByDestination(final String destination) {

        return findAllByPredicate(new Predicate<Mission>() {
            @Override
            public boolean apply(@Nullable Mission input) {

                return StringUtils.equals(input.getDestination(), destination);
            }
        });
    }

    @Override
    public List<Mission> findAllByDestinationLike(final String partialDestination) {

        return findAllByPredicate(new Predicate<Mission>() {
            @Override
            public boolean apply(@Nullable Mission input) {

                return StringUtils.containsIgnoreCase(input.getDestination(), partialDestination);
            }
        });
    }
}
