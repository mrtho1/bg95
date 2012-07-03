package com.thompson234.bg95.task;

import com.google.common.collect.ImmutableMultimap;
import com.thompson234.bg95.service.SearchService;
import com.yammer.dropwizard.tasks.Task;

import java.io.PrintWriter;

public class BuildIndexTask extends Task {

    private final SearchService _searchService;

    public BuildIndexTask(SearchService searchService) {
        super("build-index");

        _searchService = searchService;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        _searchService.buildIndex();
    }
}
