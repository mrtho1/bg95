package com.thompson234.bg95.cli;

import com.thompson234.bg95.Bg95Configuration;
import com.thompson234.bg95.model.Aircraft;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.Mission;
import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import org.apache.commons.cli.CommandLine;

import java.util.List;

public class ShowAllDataCommand extends ConfiguredCommand<Bg95Configuration> {

    public ShowAllDataCommand() {
        super("show-all", "Shows all data.");
    }

    @Override
    protected void run(AbstractService<Bg95Configuration> bg95ConfigurationAbstractService, Bg95Configuration configuration, CommandLine params) throws Exception {

        final List<Airman> airmen = configuration.getAirmanDao().findAll();
        System.out.println("=====Airmen(" + airmen.size() + ")=====");
        for (int x = 0; x < airmen.size(); ++x) {
            final Airman airman = airmen.get(x);
            System.out.println(x + ". " + airman);
        }

        final List<Aircraft> aircraft = configuration.getAircraftDao().findAll();
        System.out.println("=====Aircraft(" + aircraft.size() + ")=====");
        for (int x = 0; x < aircraft.size(); ++x) {
            final Aircraft ac = aircraft.get(x);
            System.out.println(x + ". " + ac);
        }

        final List<Mission> missions = configuration.getMissionDao().findAll();
        System.out.println("=====Missions(" + missions.size() + ")=====");
        for (int x = 0; x < missions.size(); ++x) {
            final Mission mission = missions.get(x);
            System.out.println(x + ". " + mission);
        }
    }
}
