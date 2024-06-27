package com.raredev.handlers;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.google.gson.JsonObject;
import com.raredev.base.EnvironmentVariable;
import com.raredev.base.TestBase;
import com.raredev.base.TestDataGenerator;
import com.raredev.base.TestLogger;
import com.raredev.constants.ConfigJsonKeys;
import com.raredev.constants.Constants;
import com.raredev.managers.CloudWatchLogManager;
import com.raredev.managers.MasterConfigManager;
import com.raredev.managers.TransformationConfigManager;
import com.raredev.objects.S3ObjectKeyParser;
import com.raredev.processors.CsvProcessor;
import com.raredev.processors.FileProcessor;
import com.raredev.processors.ProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;


@ExtendWith(MockitoExtension.class)
public class RegressionTest extends TestBase {

    @BeforeEach
    public void setUp(){
        EnvironmentVariable.set("test_config_base","./src/test/java/Resources/config/");
        EnvironmentVariable.set("test_input_file_path","./src/test/java/Resources/input/");
        EnvironmentVariable.set("test_output_base","./src/test/java/Resources/output/");
    }

    @Test
    public void RegressionTest(){
        try {
            cleanFolder(new File(EnvironmentVariable.get("test_output_base")+Constants.TMP_PATH));
            File dir=new File(EnvironmentVariable.get("test_input_file_path"));
            System.out.println();
            AtomicInteger fileCounter = new AtomicInteger(0);
            TestLogger.log("===> Regression Test Started: input_dir_path: "+dir.getAbsolutePath());
            for(File file: dir.listFiles()) {
                System.out.println();
                TestLogger.log("===> File"+fileCounter.addAndGet(1)+": processing file:: "+file.getName());
                String sampleS3EventPath = "./src/test/java/Resources/SampleS3Event.json";
                S3EventNotification s3Event = TestDataGenerator.getS3EventJson(sampleS3EventPath);

                //load event data
                String bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
                TestLogger.log("bucketName set: " + bucketName);
                String objectKey = Constants.UNPROCESSED_BASE+file.getName();
                TestLogger.log("objectKey set: " + objectKey);
                S3ObjectKeyParser s3ObjectKeyParser = new S3ObjectKeyParser(objectKey);
                TestLogger.log("S3ObjectKeyParser set: " + s3ObjectKeyParser);

                //load config data
                String configPath = getConfigPath(s3ObjectKeyParser);
                JsonObject obj = TestDataGenerator.getConfigurationJson(configPath);
                String configString = TestDataGenerator.getConfigStr(configPath);
                if (obj.has(ConfigJsonKeys.OUTPUT_FILE_CONFIG)) {
                    TransformationConfigManager.build(obj);
                }
                TestLogger.log("Transformations set: " + TransformationConfigManager.getTransformations());
                MasterConfigManager masterConfigManager = new MasterConfigManager(configString, objectKey);

                String inputFilePath = EnvironmentVariable.get("test_input_file_path")+file.getName();
                InputStream s3ObjectInputStream = TestDataGenerator.getTestInputStream(inputFilePath);

                //load input file data
                try {
                    FileProcessor fileProcessor = ProcessorFactory.getProcessor(s3ObjectKeyParser.getFileExtension());
                    fileProcessor.process(s3ObjectInputStream);
                    masterConfigManager.generateOutputFileData();
                } catch (Exception e) {
                    CloudWatchLogManager.getInstance().log("Exception occured while fetching processor");
                    e.printStackTrace();
                }

                String fileName = s3ObjectKeyParser.getFileNameWithoutExtension();
                String fileSuffix = fileName.substring(fileName.lastIndexOf("-") + 1);
                String outputFileName = Constants.REPORT + Constants.DASH + fileSuffix + Constants.CSV_EXT;
                String reportFilePath = EnvironmentVariable.get("test_output_base") + Constants.TMP_PATH + outputFileName;
                CsvProcessor.writeCsvFile(reportFilePath,
                        MasterConfigManager.getOutputColumnIndexMap(),
                        MasterConfigManager.getOutputFileData());
                startFileComparisionAndCleanUp(outputFileName);
            }
        } catch (IOException e) {
            TestLogger.log("Some ERROR occurred while performing RegressionTest:"+e);
            throw new RuntimeException(e);
        }
    }

    private void startFileComparisionAndCleanUp(String outputFileName) throws IOException {
        String base=EnvironmentVariable.get("test_output_base");
        File fileExpected = new File(base+outputFileName);
        File fileActual = new File(base+Constants.TMP_PATH+outputFileName);
        TestLogger.log("Starting comparison of: ");
        TestLogger.log("fileExpected: "+fileExpected.getName());
        TestLogger.log("fileActual: "+Constants.TMP_PATH+fileActual.getName());
        System.out.println();
        compareCSV(fileExpected,fileActual);
        cleanFolder(fileActual);
        MasterConfigManager.clearAll();
    }

    private String getConfigPath(S3ObjectKeyParser s3ObjectKeyParser){
        String fileExtension=s3ObjectKeyParser.getFileExtension();
        String fileName=s3ObjectKeyParser.getFileNameWithoutExtension();
        String fileSuffix=fileName.substring(fileName.lastIndexOf("-")+1);
        String configTestBase= EnvironmentVariable.get("test_config_base");
        return configTestBase+fileExtension+Constants.DASH+Constants.CONFIG+Constants.DASH+fileSuffix+Constants.JSON_EXT;
    }
}






