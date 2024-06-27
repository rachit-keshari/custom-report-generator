package com.raredev.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raredev.constants.ConfigJsonKeys;

import java.util.HashMap;
import java.util.Map;

public class RenameConfigManager {

     private static final Map<String, String> renamingRelationMap = new HashMap<>();

    public RenameConfigManager(JsonObject obj) {
         loadRenameConfigMap(obj);
    }

    public static void build(JsonObject jsonObject){
        loadRenameConfigMap(jsonObject);
    }

    private static void loadRenameConfigMap(JsonObject obj) {
         if(obj.has(ConfigJsonKeys.OUTPUT_FILE_CONFIG)) {
             for(JsonElement e : obj.getAsJsonArray(ConfigJsonKeys.OUTPUT_FILE_CONFIG)){
                 renamingRelationMap.put(e.getAsJsonObject().get(ConfigJsonKeys.NAME).getAsString(),
                         e.getAsJsonObject().get(ConfigJsonKeys.RENAME_TO).getAsString());
             }
         }
    }

    public static void applyRenaming(Map<String, String> data){
        Map<String, String> updatedData = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String originalKey = entry.getKey();
            String value = entry.getValue();
            if (renamingRelationMap.containsKey(originalKey)) {
                String newKey = renamingRelationMap.get(originalKey);
                updatedData.put(newKey, value);
            } else {
                updatedData.put(originalKey, value);
            }
        }
        data.clear();
        data.putAll(updatedData);
    }

    public static void clear(){
        renamingRelationMap.clear();
    }
}
