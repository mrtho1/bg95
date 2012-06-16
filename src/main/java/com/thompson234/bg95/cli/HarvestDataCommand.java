package com.thompson234.bg95.cli;

import com.thompson234.bg95.Bg95Configuration;
import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class HarvestDataCommand extends ConfiguredCommand<Bg95Configuration> {

    public HarvestDataCommand() {
        super("harvest-data", "Updates model stores with harvested http data.");
    }

    @Override
    public Options getOptions() {
        return super.getOptions();
//        final Option option =
//                OptionBuilder.withDescription("Invalidate the HTTP content")
//                        .isRequired(false)
//                        .hasArg(false)
//                        .create('f');
//
//        final Options options = new Options();
//        options.addOption(option);
//        return options;
    }

    @Override
    protected void run(AbstractService<Bg95Configuration> bg95ConfigurationAbstractService, Bg95Configuration configuration, CommandLine params) throws Exception {

        configuration.getHttpHarvester().harvest();
    }
}
