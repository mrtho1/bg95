package com.thompson234.bg95.model;

import java.util.Map;

public interface Model {

    public String getId();

    public Map<String, Object> summarize();

    void sanitize();
}
