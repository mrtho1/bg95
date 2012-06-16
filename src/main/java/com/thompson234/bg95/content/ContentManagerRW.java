package com.thompson234.bg95.content;

import java.io.InputStream;
import java.util.Map;

public interface ContentManagerRW extends ContentManager {
    public static final String META_ORIGINAL_KEY = "bg95.originalKey";
    public static final String META_LENGTH = "bg95.length";
    public static final String META_CONTENT_TYPE = "bg95.contentType";

    public void store(String key, InputStream toStore, Map<String, String> meta);
}
