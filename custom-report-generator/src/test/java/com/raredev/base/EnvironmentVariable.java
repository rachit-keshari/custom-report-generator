package com.raredev.base;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentVariable {
    private static final Map<String, String> environmentVariable = new HashMap<>();

    public static void set(String key, String value) {
        environmentVariable.put(key, value);
    }

    public static void clear(String key) {
        environmentVariable.remove(key);
    }

    public static void clearAll() {
        environmentVariable.clear();
    }

    public static String get(String key) {
        return environmentVariable.get(key);
    }

    public static Map<String, String> getAll() {
        return new HashMap<>(environmentVariable);
    }
}
