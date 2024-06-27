package com.raredev.managers;

import com.google.gson.*;
import com.raredev.constants.ConfigJsonKeys;
import com.raredev.constants.Constants;
import com.raredev.objects.S3ObjectKeyParser;
import lombok.Data;
import lombok.Getter;

import java.util.*;

@Data
public class MasterConfigManager {

    @Getter
    private final static Map<Integer, String> inputColumnIndexMap =new TreeMap<>();
    @Getter
    private final static List<String[]> inputFileData = new ArrayList<>();
    @Getter
    private final static Map<Integer, String> outputColumnIndexMap =new TreeMap<>();
    @Getter
    private final static List<String[]> outputFileData = new ArrayList<>();
    private final S3ObjectKeyParser s3ObjectKeyParser;
    private final String inputFileExtension;

    public MasterConfigManager(String configString, String objectKey) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = gson.fromJson(configString, JsonObject.class);
        this.s3ObjectKeyParser=new S3ObjectKeyParser(objectKey);
        if(obj.has(ConfigJsonKeys.INPUT_FILE_EXTENSION)){
            this.inputFileExtension=obj.get(ConfigJsonKeys.INPUT_FILE_EXTENSION).getAsString();
        }else{
            this.inputFileExtension=Constants.CSV;
        }
        loadConfig(obj);
    }

    public static void loadConfig(JsonObject obj){
        if(obj.has(ConfigJsonKeys.OUTPUT_FILE_HEADERS)){
            JsonArray jsonArray = obj.get(ConfigJsonKeys.OUTPUT_FILE_HEADERS).getAsJsonArray();
            int i=0;
            for(JsonElement jsonElement: jsonArray){
                outputColumnIndexMap.put(i++,jsonElement.getAsString());
            }
        }
        if(obj.has(ConfigJsonKeys.OUTPUT_FILE_CONFIG)){
            TransformationConfigManager.build(obj);
            RenameConfigManager.build(obj);
        }

    }

    public void generateOutputFileData(){
        try {
            for(String[] dataArray:inputFileData){
                Map<String, String> dataMap = new HashMap<>();
                for(int i=0;i<dataArray.length;i++){
                    dataMap.put(inputColumnIndexMap.get(i),dataArray[i]);
                }
                TransformationConfigManager.applyTransformation(dataMap);
                RenameConfigManager.applyRenaming(dataMap);
                String[] outputData=new String[outputColumnIndexMap.size()];
                int i=0;
                for(Map.Entry<Integer, String> outputHeader: outputColumnIndexMap.entrySet()){
                    outputData[i++]=dataMap.get(outputHeader.getValue());
                }
                outputFileData.add(outputData);
                CloudWatchLogManager.getInstance().log("===> outputColumnIndexMap has been Initialized");
                CloudWatchLogManager.getInstance().log("===> outputFileData List has been Initialized");
            }
        } catch(Exception e){
            CloudWatchLogManager.getInstance().log("Exception occured while GeneratingCustomReport");
            e.printStackTrace();
        }
    }

    public static void clearAll(){
        inputFileData.clear();
        inputColumnIndexMap.clear();
        outputFileData.clear();
        outputColumnIndexMap.clear();
        TransformationConfigManager.clear();
        RenameConfigManager.clear();
        CloudWatchLogManager.getInstance().log("===> Cleared all garbage values from internal memory");
    }

}
