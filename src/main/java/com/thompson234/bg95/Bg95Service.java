package com.thompson234.bg95;

import com.google.common.cache.CacheBuilderSpec;
import com.thompson234.bg95.cli.DisplayAirmanMissionsCommand;
import com.thompson234.bg95.cli.DisplayAirmanNamesCommand;
import com.thompson234.bg95.cli.HarvestDataCommand;
import com.thompson234.bg95.cli.ShowAllDataCommand;
import com.thompson234.bg95.health.DaoHealthCheck;
import com.thompson234.bg95.resource.AircraftResource;
import com.thompson234.bg95.resource.AirmanResource;
import com.thompson234.bg95.resource.CountResource;
import com.thompson234.bg95.resource.MissionResource;
import com.thompson234.bg95.resource.SearchResource;
import com.thompson234.bg95.web.IndexFilter;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;

public class Bg95Service extends Service<Bg95Configuration> {

    public static void main(String[] args) throws Exception {
        new Bg95Service().run(args);
    }

    private Bg95Service() {
        super("bg95");

        addCommand(new DisplayAirmanMissionsCommand());
        addCommand(new DisplayAirmanNamesCommand());
        addCommand(new HarvestDataCommand());
        addCommand(new ShowAllDataCommand());

        addBundle(new AssetsBundle("/assets", CacheBuilderSpec.disableCaching(), "/"));
    }

    @Override
    protected void initialize(Bg95Configuration configuration, Environment environment) throws Exception {

        environment.addFilter(new IndexFilter(), "/*");

        environment.addHealthCheck(new DaoHealthCheck(configuration.getAirmanDao(),
                configuration.getAircraftDao(),
                configuration.getMissionDao()));
        environment.addResource(new AircraftResource(configuration.getAircraftDao()));
        environment.addResource(new AirmanResource(configuration.getAirmanDao()));
        environment.addResource(new MissionResource(configuration.getMissionDao()));
        environment.addResource(new SearchResource(configuration.getAirmanDao(),
                configuration.getAircraftDao(),
                configuration.getMissionDao()));
        environment.addResource(new CountResource(configuration.getAirmanDao(),
                configuration.getAircraftDao(),
                configuration.getMissionDao()));
    }
}
