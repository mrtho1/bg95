package com.thompson234.bg95.json;

import com.yammer.dropwizard.json.Json;
import org.codehaus.jackson.map.ObjectMapper;

public class Bg95Json extends Json {

    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
