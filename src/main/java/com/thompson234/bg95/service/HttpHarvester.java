package com.thompson234.bg95.service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.thompson234.bg95.content.ContentManager;
import com.thompson234.bg95.dao.AircraftDao;
import com.thompson234.bg95.dao.AirmanDao;
import com.thompson234.bg95.dao.MissionDao;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.AircraftNumber;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.CrewAssignment;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.Name;
import com.thompson234.bg95.model.Sortie;
import com.thompson234.bg95.util.Utils;
import com.yammer.dropwizard.logging.Log;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpHarvester {
    private static final Log _sLog = Log.forClass(HttpHarvester.class);

    private static final String UNKNOWN_AIRCRAFT_NUMBER = "*unknown";
    private static final String HOME_ALT = "95th Bomb Group Horham";
    private static final String BUSTED_IMAGE_HREF_DETECTOR = "95th_joomla/images/noseart";

    private static final String SCRAPE_ROOT = "http://95thbg.org";
    private static final String AIRMAN_URL_FORMAT = "http://95thbg.org/95th_db_pages/left_formation_code/gethonoroll.php?searchname=%s&goButton=Submit";
    private static final String MISSION_URL = "http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=123&Itemid=420";
    private static final String AIRMAN_MISSION_URL_FORMAT = "http://95thbg.org/95th_db_pages/left_formation_code/getdetail.php?searchname=%s&goButton=Submit";
    private static final String AIRCRAFT_URL_FORMAT = "http://95thbg.org/95th_db_pages/left_formation_code/getplane.php?searchname=%s&goButton=Submit";
    private static final String FLIGHT_CREW_PHOTO_URL = "http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=115&Itemid=244";

    private static final ImmutableList<String> AIRCRAFT_IMAGE_URLS = ImmutableList.<String>builder()
            .add("http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=276&Itemid=377")
            .add("http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=277&Itemid=378")
            .add("http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=278&Itemid=379")
            .add("http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=279&Itemid=380")
            .add("http://95thbg.org/95th_joomla/index.php?option=com_content&view=article&id=275&Itemid=376").build();

    private static final String DATE_FORMAT = "MM/dd/yy";

    private ContentManager _httpContentManager;
    private AircraftDao _aircraftDao;
    private AirmanDao _airmanDao;
    private MissionDao _missionDao;

    private HtmlCleaner _cleaner = new HtmlCleaner();
    private Map<Name, Airman> _airmanCache = Maps.newTreeMap();
    private Map<Integer, Mission> _missionCache = Maps.newTreeMap();
    private Map<AircraftNumber, Aircraft> _aircraftCache = Maps.newHashMap();
    private Multimap<String, String> _aircraftImageUrlMap = HashMultimap.create();

    @Inject
    public HttpHarvester(@Named("httpContentContentManager") ContentManager httpContentManager,
                         AircraftDao aircraftDao,
                         AirmanDao airmanDao,
                         MissionDao missionDao) {

        _httpContentManager = httpContentManager;
        _aircraftDao = aircraftDao;
        _airmanDao = airmanDao;
        _missionDao = missionDao;
    }

    private TagNode cleanHtml(InputStream in) {
        try {
            return _cleaner.clean(in);
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private InputStream loadHtml(String url) {
        try {
            return _httpContentManager.load(url);
        } catch (Exception ex) {
            _sLog.error(ex, "Error loading URL: {}", url);
            throw Throwables.propagate(ex);
        }
    }

    private boolean isImageHref(String url) {

        return url != null && (url.endsWith("gif") || url.endsWith("jpg") || url.endsWith("JPG") || url.endsWith("png"));
    }

    private boolean isAbsoluteUrl(String url) {
        return url != null && url.startsWith("http://");
    }

    private String checkBustedImageHrefFix(String href) {
        //http://95thbg.org/95th_joomla/images/noseart/http:/95thbg.org/95th_joomla/images/noseart/HolyTerror.jpg
        //convert to
        //http://95thbg.org/95th_joomla/images/noseart/HolyTerror.jpg
        String toCaller = href;

        if (href.indexOf(BUSTED_IMAGE_HREF_DETECTOR) != href.lastIndexOf(BUSTED_IMAGE_HREF_DETECTOR)) {
            toCaller = SCRAPE_ROOT + "/" + href.substring(href.lastIndexOf(BUSTED_IMAGE_HREF_DETECTOR));
        }

        return toCaller;
    }

    private String convertToAbsoluteUrl(String source, String url) {

        if (isAbsoluteUrl(url)) {
            return url;
        }

        String toCaller = null;
        if (url.startsWith("/")) {
            URI uri = URI.create(source);
            toCaller = source.substring(0, source.indexOf(uri.getPath()));
        } else {
            toCaller = source.substring(0, source.lastIndexOf("/") + 1);
        }

        toCaller += url;
        return toCaller;
    }

    private void harvestAircraftImageUrls() {

        for (String url : AIRCRAFT_IMAGE_URLS) {

            final TagNode root = cleanHtml(loadHtml(url));
            final ImmutableList<TagNode> preNodes = Utils.selectAllNodes(root, "//div[@class='article']//pre");

            for (TagNode pre : preNodes) {
                final String aircraftNumber = Utils.sanitizeSpaces(pre.getText().toString()).split(" ")[0].trim();

                final ImmutableList<TagNode> anchors = Utils.selectAllNodes(pre, "//a");

                for (TagNode anchor : anchors) {

                    final String originalHref = anchor.getAttributeByName("href");
                    String href = originalHref;

                    if (Strings.isNullOrEmpty(href)) {
                        continue;
                    }

                    href = convertToAbsoluteUrl(url, href);
                    final String acNumberOrName = Utils.sanitizeTagNodeText(anchor);

                    if (isImageHref(href)) {
                        href = checkBustedImageHrefFix(href);
                        _aircraftImageUrlMap.put(aircraftNumber, href);
                        _aircraftImageUrlMap.put(acNumberOrName, href);
                    } else {
                        //This is a link to an info page.
                        TagNode infoRoot = null;

                        try {
                            infoRoot = cleanHtml(loadHtml(href));
                        } catch (Exception ex) {
                            _sLog.warn(ex, "Failed to get url:{} original:{}", href, originalHref);
                            continue;
                        }

                        final ImmutableList<TagNode> imgNodes = Utils.selectAllNodes(infoRoot, "//img");

                        for (TagNode img : imgNodes) {
                            final String alt = img.getAttributeByName("alt");

                            if (HOME_ALT.equals(alt)) {
                                continue;
                            }

                            String src = img.getAttributeByName("src");
                            src = convertToAbsoluteUrl(href, src);

                            _aircraftImageUrlMap.put(aircraftNumber, src);
                            _aircraftImageUrlMap.put(acNumberOrName, src);
                        }
                    }
                }
            }
        }
    }

    private void harvestFlightCrewImageUrls() {

        final TagNode root = cleanHtml(loadHtml(FLIGHT_CREW_PHOTO_URL));

        final ImmutableList<TagNode> anchors = Utils.selectAllNodes(root, "//div[@class='article']//a");

        for (TagNode anchor : anchors) {
            //Case 1:Abwender, Don 2............334th Sqn
            //Case 2:Bek, Thomas, G  1............412th Sqn
            //Case 3:Ross, J.R..........................336th Sqn
            //Case 4:Brown Alvin H  1................412th Sqn
            //Case 5:Shepard, John R "Jack"......334th Sqn

            final String anchorText = Utils.sanitizeTagNodeText(anchor);

            if (anchorText == null) {
                continue;
            }

            int pilotSplit = anchorText.indexOf("..");
            if (pilotSplit == -1) {

                pilotSplit = anchorText.lastIndexOf(".");

                if (pilotSplit == -1) {
                    _sLog.debug("Bad anchor text {}", anchorText);
                    continue;
                }
            }

            String pilotNameText = anchorText.substring(0, pilotSplit).trim();

            //Case 1, 2, 4
            if (Character.isDigit(pilotNameText.charAt(pilotNameText.length() - 1))) {
                //strip off the number if needed
                pilotNameText = pilotNameText.substring(0, pilotNameText.length() - 1).trim();
            }

            //Case 3
            if (pilotNameText.endsWith("J.R")) {
                pilotNameText = pilotNameText + ".";
            }

            //Case 5 should work, it won't produce a known name though.
            final Splitter splitter = Splitter.on(CharMatcher.anyOf(", ")).omitEmptyStrings().trimResults().limit(2);
            final List<String> tokens = Lists.newArrayList();
            for (String token : splitter.split(pilotNameText)) {
                tokens.add(0, token);
            }

            if (tokens.size() != 2) {
                _sLog.debug("Could not parse pilot name {}", pilotNameText);
                continue;
            }

            pilotNameText = Joiner.on(" ").skipNulls().join(tokens);
            final Name pilotName = new Name(pilotNameText);

            String href = anchor.getAttributeByName("href");
            href = convertToAbsoluteUrl(FLIGHT_CREW_PHOTO_URL, href);

            Airman airman = _airmanCache.get(pilotName);

            if (airman == null) {
                _sLog.debug("Could not find airman: {}", pilotName);
                for (Map.Entry<Name, Airman> entry : _airmanCache.entrySet()) {
                    if (pilotName.softMatch(entry.getKey())) {
                        _sLog.debug("Using soft match {} for {}", entry.getKey(), pilotName);
                        airman = entry.getValue();
                    }
                }
            }

            if (airman != null) {
                airman.imageUrl(href);
            }
        }
    }

    private void harvestMissions() {

        final TagNode root = cleanHtml(loadHtml(MISSION_URL));
        final ImmutableList<TagNode> dataRows = Utils.selectAllNodes(root, "//div[@class='article']//tr[position() > 8]");

        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        for (TagNode row : dataRows) {
            final ImmutableList<String> fields = Utils.selectAllStrings(row, "//td/text()");

            try {
                final int number = Integer.parseInt(fields.get(0));
                final Date date = dateFormat.parse(fields.get(1));
                final String destination = fields.get(3);
                final String stats = fields.get(4);

                _missionCache.put(number, new Mission().number(number).date(date).destination(destination).stats(stats));
            } catch (Exception ex) {
                throw Throwables.propagate(ex);
            }
        }
    }

    private void harvestAirmen() {

        for (char query = 'a'; query <= 'z'; ++query) {
            final String url = String.format(AIRMAN_URL_FORMAT, query);
            final TagNode root = cleanHtml(loadHtml(url));

            final ImmutableList<TagNode> dataRows = Utils.selectAllNodes(root, "//tr[position() > 1]");

            for (TagNode row : dataRows) {

                final ImmutableList<String> fields = Utils.selectAllStrings(row, "//td/text()");
                final String nameText = fields.get(0);
                final Name airmanName = new Name(nameText);
                Airman airman = _airmanCache.get(airmanName);

                if (airman == null) {
                    airman = new Airman().name(airmanName);
                    _airmanCache.put(airmanName, airman);
                }

                airman.rank(fields.get(1)).role(fields.get(2)).note(fields.get(3)).unit(fields.get(4));
            }
        }
    }

    private Aircraft getAircraft(String aircraftId) {

        final AircraftNumber serialNumber = new AircraftNumber(aircraftId);
        _sLog.debug("Searching for aircraft {}", serialNumber);

        Aircraft aircraft = _aircraftCache.get(serialNumber);
        if (aircraft == null) {
            final String url = String.format(AIRCRAFT_URL_FORMAT, Utils.urlEncode(serialNumber.getSearchForm()));
            final TagNode root = cleanHtml(loadHtml(url));

            final ImmutableList<TagNode> dataRows = Utils.selectAllNodes(root, "//tr[position() > 1]");

            aircraft = new Aircraft().number(aircraftId);

            if (dataRows.isEmpty()) {
                _sLog.debug("Could not find aircraft {}", aircraftId);
            }

            for (TagNode row : dataRows) {
                final ImmutableList<String> fields = Utils.selectAllStrings(row, "//td/text()");
                final AircraftNumber candidateSn = new AircraftNumber(fields.get(0));
                if (!serialNumber.equals(candidateSn)) {
                    _sLog.debug("Candidate aircraft serial number {} not same as requested serial number {}, ignoring.", candidateSn, serialNumber);
                    continue;
                }

                aircraft.setNumber(serialNumber.getSerialNumber());
                aircraft.name(fields.get(1));
                aircraft.squadron(fields.get(2));
                aircraft.callsign(fields.get(3));
                aircraft.setModel(fields.get(4));
            }

            final Set<String> imageUrls = Sets.newHashSet();
            imageUrls.addAll(_aircraftImageUrlMap.get(aircraft.getNumber()));
            for (String name : aircraft.getNames()) {
                imageUrls.addAll(_aircraftImageUrlMap.get(name));
            }
            aircraft.imageUrls(imageUrls);

            _aircraftCache.put(serialNumber, aircraft);
        }

        return aircraft;
    }

    private void harvestAirmanMissions(Airman airman) {
        final String url = String.format(AIRMAN_MISSION_URL_FORMAT, Utils.urlEncode(airman.getFullName()));
        final TagNode root = cleanHtml(loadHtml(url));

        final ImmutableList<TagNode> dataRows = Utils.selectAllNodes(root, "//tr[position() > 1]");

        _sLog.trace("Processing mission data for {}", airman.getFullName());

        for (TagNode row : dataRows) {

            final ImmutableList<String> fields = Utils.selectAllStrings(row, "//td/text()");
            final int missionNumber = Integer.parseInt(fields.get(1));
            final String aircraftId = fields.get(3);
            final String fullName = fields.get(5);
            final String position = fields.get(6);
            final String status = fields.get(7);

            if (!fullName.equals(airman.getFullName())) {
                continue;
            }

            final Mission mission = _missionCache.get(missionNumber);

            if (mission == null) {
                continue;
            }

            final Aircraft aircraft = getAircraft(aircraftId);

            if (aircraft == null) {
                continue;
            }

            Sortie sortie = mission.getSortieByNumber(aircraft.getNumber());

            if (sortie == null) {
                sortie = new Sortie().aircraft(aircraft);
                mission.sortie(sortie);
            }

            final CrewAssignment crewAssignment = new CrewAssignment().airman(airman).role(position).status(status);
            sortie.crewAssignment(crewAssignment);
        }
    }

    private void extrapolateCrewImageUrls() {

        for (Mission mission : _missionCache.values()) {
            for (Sortie sortie : mission.getSorties()) {

                final Airman pilot = sortie.getCrewByRole("pilot");

                if (pilot != null) {

                    for (Airman crew : sortie.getFlightCrew()) {

                        if (!crew.equals(pilot)) {

                            for (String imageUrl : pilot.getImageUrls()) {
                                crew.imageUrl(imageUrl);
                            }
                        }
                    }
                }
            }
        }
    }

    public void harvest() {
        _airmanDao.deleteAll();
        _aircraftDao.deleteAll();
        _missionDao.deleteAll();

        harvestAircraftImageUrls();
        harvestAirmen();
        harvestFlightCrewImageUrls();

        harvestMissions();
        for (Airman airman : _airmanCache.values()) {
            harvestAirmanMissions(airman);
        }

        extrapolateCrewImageUrls();

        _airmanDao.saveAll(_airmanCache.values());
        _aircraftDao.saveAll(_aircraftCache.values());
        _missionDao.saveAll(_missionCache.values());
    }
}
