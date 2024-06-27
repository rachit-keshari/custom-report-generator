package com.raredev.base;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.raredev.managers.CloudWatchLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class TestDataGenerator {

        public static JsonObject getConfigurationJson(String configPath) throws IOException {
            String configJsonStr = Files.readString(Paths.get(configPath));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(configJsonStr, JsonObject.class);
        }

        public static String getConfigStr(String configPath) throws IOException {
            return Files.readString(Paths.get(configPath));
        }

        public static InputStream getTestInputStream(String filePath) {
            try {
                return Files.newInputStream(Paths.get(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    public static S3EventNotification getS3EventJson(String sampleEventPath) throws IOException {
        String s3EventString = Files.readString(Paths.get(sampleEventPath));
        return S3EventNotification.parseJson(s3EventString);
    }

        public static LambdaLogger generateFakeCloudWatchLogger() {
            Logger log4jLogger = LoggerFactory.getLogger(CloudWatchLogManager.class);
            return new LambdaLogger() {
                @Override
                public void log(String s) {
                    log4jLogger.info(s);
                }

                @Override
                public void log(byte[] bytes) {
                    log4jLogger.info(Arrays.toString(bytes));
                }
            };

        }
}
