package com.raredev.managers;

import com.google.gson.JsonObject;
import com.raredev.base.TestBase;
import com.raredev.base.TestDataGenerator;
import com.raredev.base.TestLogger;
import com.raredev.constants.ConfigJsonKeys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class TransformationConfigManagerTest extends TestBase {

    @BeforeEach
    void setUp() {
    }

    @Test
    void loadTransformationTest() {
        try {
            String configPath = "./src/test/java/Resources/config/csv-config-243.json";
            JsonObject obj = TestDataGenerator.getConfigurationJson(configPath);
            if(obj.has(ConfigJsonKeys.OUTPUT_FILE_CONFIG)) {
                TransformationConfigManager.build(obj);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Assertions.assertEquals(TransformationConfigManager.getTransformations().size(),3);
            TestLogger.log("Transformations.size matched");
            TestLogger.log("Transformations set:"+TransformationConfigManager.getTransformations());
        }
    }
}
