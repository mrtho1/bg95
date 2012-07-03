package com.thompson234.bg95;

import org.codehaus.jackson.annotate.JsonProperty;

public class SearchConfiguration {

    @JsonProperty("indexDir")
    private String _indexDir = "./temp/index";

    public String getIndexDir() {
        return _indexDir;
    }
}
