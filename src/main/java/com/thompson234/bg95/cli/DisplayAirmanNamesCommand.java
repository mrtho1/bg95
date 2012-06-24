package com.thompson234.bg95.cli;

import com.thompson234.bg95.Bg95Configuration;
import com.thompson234.bg95.model.Airman;
import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import org.apache.commons.cli.CommandLine;

public class DisplayAirmanNamesCommand extends ConfiguredCommand<Bg95Configuration> {

    public DisplayAirmanNamesCommand() {
        super("airman-names", "Displays mission data for a given airman.");
    }

    @Override
    protected void run(AbstractService<Bg95Configuration> service,
                       Bg95Configuration configuration,
                       CommandLine params) throws Exception {

        for (Airman airman : configuration.getAirmanDao().findAll()) {
            System.out.println(airman.getFullName());
        }
    }
}
