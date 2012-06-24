package com.thompson234.bg95.dao.impl;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.CrewAssignment;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.Sortie;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

public class SdbMissionDaoImpl extends AbstractSdbModelDao<Mission> implements MissionDao {

    private static final String CREW_ASSIGNMENT_NAME_FORMAT = "%s::%s::%s";

    private static final String NUMBER = "number";
    private static final String DATE = "date";
    private static final String DESTINATION = "destination";
    private static final String TOOK_OFF = "tookOff";
    private static final String COMPLETED = "completed";
    private static final String DAMAGED = "damaged";
    private static final String LOST = "lost";
    private static final String SALVAGED = "salvaged";
    private static final String ROLE = "role";
    private static final String STATUS = "status";

    private final AirmanDao _airmanDao;
    private final AircraftDao _aircraftDao;

    private Cache<String, Mission> _cache = CacheBuilder.newBuilder().initialCapacity(400).recordStats().build();

    @Inject
    public SdbMissionDaoImpl(AmazonSimpleDBClient client,
                             @Named("domain.mission.name") String domainName,
                             @Named("domain.mission.forceReset") boolean forceReset,
                             @Named("domain.mission.preCache") boolean preCache,
                             AirmanDao airmanDao,
                             AircraftDao aircraftDao) {
        super(client, domainName, forceReset);
        _airmanDao = airmanDao;
        _aircraftDao = aircraftDao;

        if (preCache) {
            findAll();
        }
    }

    @Override
    protected Mission getCachedObject(String id) {
        return _cache.getIfPresent(id);
    }

    @Override
    protected void invalidateCache(String id) {
        _cache.invalidate(id);
    }

    @Override
    protected void cacheObject(Mission model) {
        _cache.put(model.getId(), model);
    }

    @Override
    protected String getSelectOneStatement(String id) {
        return String.format("select * from %s where itemName() like '%s'", getDomainName(), id);
    }

    @Override
    protected List<Mission> fromItems(List<Item> items) {

        final Map<String, Mission> missionMap = Maps.newHashMap();

        for (Item item : items) {

            final String[] parts = item.getName().split("::");
            Mission mission = missionMap.get(parts[0]);

            if (mission == null) {
                mission = new Mission();
                mission.setId(parts[0]);
                missionMap.put(parts[0], mission);
            }

            Sortie sortie = null;

            if (parts.length > 1) {
                sortie = mission.getSortieById(parts[1]);

                if (sortie == null) {
                    sortie = new Sortie();
                    sortie.setAircraft(_aircraftDao.findById(parts[1]));
                }

                mission.sortie(sortie);
            }

            CrewAssignment ca = null;

            if (parts.length > 2) {
                ca = sortie.getCrewAssignmentByAirmanId(parts[2]);

                if (ca == null) {
                    ca = new CrewAssignment();
                    ca.setAirman(_airmanDao.findById(parts[2]));
                    sortie.crewAssignment(ca);
                }
            }

            for (Attribute attribute : item.getAttributes()) {
                final String name = attribute.getName();
                final String value = attribute.getValue();

                if (NUMBER.equals(name)) {
                    mission.number(Integer.parseInt(value));
                } else if (DATE.equals(name)) {
                    final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
                    final DateTime dateTime = formatter.parseDateTime(value);
                    mission.date(dateTime.toDate());
                } else if (DESTINATION.equals(name)) {
                    mission.destination(value);
                } else if (TOOK_OFF.equals(name)) {
                    mission.tookOff(Integer.parseInt(value));
                } else if (COMPLETED.equals(name)) {
                    mission.completed(Integer.parseInt(value));
                } else if (DAMAGED.equals(name)) {
                    mission.damaged(Integer.parseInt(value));
                } else if (LOST.equals(name)) {
                    mission.lost(Integer.parseInt(value));
                } else if (SALVAGED.equals(name)) {
                    mission.salvaged(Integer.parseInt(value));
                } else if (ROLE.equals(name)) {
                    ca.role(value);
                } else if (name.endsWith(STATUS)) {
                    ca.status(value);
                }
            }
        }

        return Lists.newArrayList(missionMap.values());
    }

    @Override
    protected List<ReplaceableItem> toReplaceableItems(Mission model) {

        List<ReplaceableItem> items = Lists.newArrayList();

        final ReplaceableItem root = new ReplaceableItem(model.getId());
        items.add(root);

        final List<ReplaceableAttribute> attributes = Lists.newArrayList();
        attributes.add(new ReplaceableAttribute(NUMBER, StringUtils.leftPad("" + model.getNumber(), 4, '0'), true));
        final DateTime dateTime = new DateTime(model.getDate(), DateTimeZone.forID("Etc/Zulu"));
        attributes.add(new ReplaceableAttribute(DATE, dateTime.toString(), true));
        attributes.add(new ReplaceableAttribute(DESTINATION, model.getDestination(), true));
        attributes.add(new ReplaceableAttribute(TOOK_OFF, StringUtils.leftPad("" + model.getTookOff(), 4, '0'), true));
        attributes.add(new ReplaceableAttribute(COMPLETED, StringUtils.leftPad("" + model.getCompleted(), 4, '0'), true));
        attributes.add(new ReplaceableAttribute(DAMAGED, StringUtils.leftPad("" + model.getDamaged(), 4, '0'), true));
        attributes.add(new ReplaceableAttribute(LOST, StringUtils.leftPad("" + model.getLost(), 4, '0'), true));
        attributes.add(new ReplaceableAttribute(SALVAGED, StringUtils.leftPad("" + model.getSalvaged(), 4, '0'), true));

        for (Sortie sortie : model.getSorties()) {

            final String acId = sortie.getAircraft().getId();

            for (CrewAssignment ca : sortie.getCrewAssignments()) {
                final String crewId = ca.getAirman().getId();
                final String role = ca.getRole();
                final String status = ca.getStatus();

                final ReplaceableItem caItem = new ReplaceableItem(String.format(CREW_ASSIGNMENT_NAME_FORMAT, model.getId(), acId, crewId));
                items.add(caItem);

                final List<ReplaceableAttribute> caAttributes = Lists.newArrayList();
                if (!StringUtils.isEmpty(role)) {
                    caAttributes.add(new ReplaceableAttribute().withName(ROLE).withValue(role).withReplace(true));
                }

                if (!StringUtils.isEmpty(status)) {
                    caAttributes.add(new ReplaceableAttribute().withName(STATUS).withValue(status).withReplace(true));
                }

                caItem.setAttributes(caAttributes);
            }
        }

        root.setAttributes(attributes);
        return items;
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
