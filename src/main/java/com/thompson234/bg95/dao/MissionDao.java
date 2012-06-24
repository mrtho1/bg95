package com.thompson234.bg95.dao;

import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Mission;

import java.util.List;

public interface MissionDao extends ModelDao<Mission> {

    List<Mission> findAllByAirman(Airman airman);

    List<Mission> findAllByAircraft(Aircraft aircraft);

    List<Mission> findAllByDestination(String destination);

    List<Mission> findAllByDestinationLike(String partialDestination);
}
