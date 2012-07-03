package com.thompson234.bg95.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JacksonModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        return objectMapper;
    }
}
