package com.raredev.base;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.raredev.constants.Constants;
import com.raredev.managers.CloudWatchLogManager;
import com.raredev.plugins.TransformationPluginsBase;
import com.raredev.plugins.TransformationPluginsExtended;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBase {

    @BeforeEach
    public void setup() {
        TestLogger.log("TransformationPlugins invoked :");
        System.out.println(Arrays.toString(TransformationPluginsExtended.values()));
        System.out.println(Arrays.toString(TransformationPluginsBase.values()));

        TestLogger.log("Setting Test EnvironmentVariables :");
        EnvironmentVariable.set("bucket_name", "raredev-bucket");

        LambdaLogger lambdaLogger = TestDataGenerator.generateFakeCloudWatchLogger();
        CloudWatchLogManager.build(lambdaLogger);
        TestLogger.log("Test CloudWatchLogManager Initialized :");

        System.out.println("\nExecuting test : " + this.getClass().getSimpleName());
    }

    @AfterEach
    public void tearDown() {
        EnvironmentVariable.clearAll();
        TestLogger.log("Cleared EnvironmentVariables :");
    }

    protected void cleanFolder(File f) throws IOException {
        TestLogger.log("Going to delete file: "+f.getAbsolutePath());
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            for (File child : Objects.requireNonNull(f.listFiles())) {
                cleanFolder(child);
            }
        }
        Files.delete(f.toPath());
        System.out.println("Deleted file: " + f.getName());
        Path parent = Path.of(f.getParent());
        Files.delete(parent);
        System.out.println("Deleted directory: " + parent);
    }

    protected List<String> readCSV(File f) throws IOException {
        List<String> lines = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(f); final BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    protected void compareCSV(File f1, File f2) throws IOException {
        System.out.println("Comparing " + f1.getAbsolutePath() + " and " + f2.getAbsolutePath());
        if (!f2.exists()) {
            throw new FileNotFoundException("Expected output file not found, please add :" + f2.getAbsolutePath());
        }
        List<String> o1 = readCSV(f1);
        List<String> o2 = readCSV(f2);
        assertEquals(o2.size(), o1.size(),"Comparing line count");
        for (int i = 0; i < o1.size(); i++) {
            assertEquals(o2.get(i), o1.get(i),"Comparing data in each row");
        }
    }

}
