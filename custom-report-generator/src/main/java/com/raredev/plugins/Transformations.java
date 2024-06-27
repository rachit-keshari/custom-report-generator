package com.raredev.plugins;

import java.util.HashMap;
import java.util.Map;

public interface Transformations {
    @SuppressWarnings("java:S2386")
    Map<String, Transformations> lookup = new HashMap<>();

    public static Transformations getFieldTransformationPlugins(String key) {
        return lookup.get(key);
    }

    String resolve(String key, Map<String, String> data, String... params);
}
