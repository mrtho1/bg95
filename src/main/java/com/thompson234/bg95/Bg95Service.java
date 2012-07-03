package com.thompson234.bg95;

import com.google.common.cache.CacheBuilderSpec;
import com.thompson234.bg95.cli.BuildIndexCommand;
import com.thompson234.bg95.cli.DisplayAirmanMissionsCommand;
import com.thompson234.bg95.cli.DisplayAirmanNamesCommand;
import com.thompson234.bg95.cli.HarvestDataCommand;
import com.thompson234.bg95.cli.ShowAllDataCommand;
import com.thompson234.bg95.health.DaoHealthCheck;
import com.thompson234.bg95.health.IndexHealthCheck;
import com.thompson234.bg95.json.Bg95Json;
import com.thompson234.bg95.json.JacksonViewMessageBodyWriter;
import com.thompson234.bg95.json.Views;
import com.thompson234.bg95.resource.AircraftResource;
import com.thompson234.bg95.resource.AirmanResource;
import com.thompson234.bg95.resource.CountResource;
import com.thompson234.bg95.resource.MissionResource;
import com.thompson234.bg95.resource.SearchResource;
import com.thompson234.bg95.task.BuildIndexTask;
import com.thompson234.bg95.web.IndexFilter;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.json.Json;
import org.codehaus.jackson.map.Module;

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
        addCommand(new BuildIndexCommand());

        addBundle(new AssetsBundle("/assets", CacheBuilderSpec.disableCaching(), "/"));
    }

    @Override
    protected void initialize(Bg95Configuration configuration, Environment environment) throws Exception {

        final Bg95Json json = (Bg95Json) getJson();
        environment.addProvider(new JacksonViewMessageBodyWriter(json.getObjectMapper(), Views.Summary.class));

        environment.addFilter(new IndexFilter(), "/*");

        environment.addTask(new BuildIndexTask(configuration.getSearchService()));

        environment.addHealthCheck(new DaoHealthCheck(configuration.getAirmanDao(),
                configuration.getAircraftDao(),
                configuration.getMissionDao()));
        environment.addHealthCheck(new IndexHealthCheck(configuration.getDirectory()));
        environment.addResource(new AircraftResource(configuration.getAircraftDao()));
        environment.addResource(new AirmanResource(configuration.getAirmanDao()));
        environment.addResource(new MissionResource(configuration.getMissionDao()));
        environment.addResource(new SearchResource(configuration.getSearchService(),
                configuration.getAircraftDao(),
                configuration.getAirmanDao(),
                configuration.getMissionDao()));
        environment.addResource(new CountResource(configuration.getAirmanDao(),
                configuration.getAircraftDao(),
                configuration.getMissionDao()));
    }

    @Override
    public Json getJson() {

        final Bg95Json json = new Bg95Json();
        for (Module module: getJacksonModules()) {
            json.registerModule(module);
        }
        return json;
    }
}
