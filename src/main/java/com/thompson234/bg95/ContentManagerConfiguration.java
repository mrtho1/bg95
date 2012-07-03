package com.thompson234.bg95;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ContentManagerConfiguration {

    @NotEmpty
    @JsonProperty("contentRoot")
    private String _contentRoot = "./temp/cache";

    @NotEmpty
    @JsonProperty("modelCacheName")
    private String _modelCacheName = "model";

    @NotEmpty
    @JsonProperty("httpCacheName")
    private String _httpCacheName = "http";

    @JsonProperty("modelDeepWrites")
    private boolean _modelDeepWrites = false;

    @JsonProperty("httpDeepWrites")
    private boolean _httpDeepWrites = false;

    @JsonProperty("modelPropagateLoadedContent")
    private boolean _modelPropagateLoadedContent = false;

    @JsonProperty("httpPropagateLoadedContent")
    private boolean _httpPropagateLoadedContent = false;

    @JsonProperty("cacheNice")
    private long _cacheNice = 10 * 1000;

    public String getContentRoot() {
        return _contentRoot;
    }

    public String getModelCacheName() {
        return _modelCacheName;
    }

    public String getHttpCacheName() {
        return _httpCacheName;
    }

    public boolean isModelDeepWrites() {
        return _modelDeepWrites;
    }

    public boolean isModelPropagateLoadedContent() {
        return _modelPropagateLoadedContent;
    }

    public boolean isHttpDeepWrites() {
        return _httpDeepWrites;
    }

    public boolean isHttpPropagateLoadedContent() {
        return _httpPropagateLoadedContent;
    }

    public long getCacheNice() {
        return _cacheNice;
    }
}
