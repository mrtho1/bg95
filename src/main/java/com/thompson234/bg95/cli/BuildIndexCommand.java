package com.thompson234.bg95.cli;

import com.thompson234.bg95.Bg95Configuration;
import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import org.apache.commons.cli.CommandLine;

public class BuildIndexCommand extends ConfiguredCommand<Bg95Configuration> {

    public BuildIndexCommand() {
        super("build-index", "Rebuilds the search index based on the current model.");
    }

    @Override
    protected void run(AbstractService<Bg95Configuration> bg95ConfigurationAbstractService,
                       Bg95Configuration configuration,
                       CommandLine params) throws Exception {

        configuration.getSearchService().buildIndex();
    }
}