package com.raredev.handlers;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.util.IOUtils;
import com.raredev.constants.Constants;
import com.raredev.managers.AmazonServiceManager;
import com.raredev.managers.CloudWatchLogManager;
import com.raredev.managers.MasterConfigManager;
import com.raredev.managers.TransformationConfigManager;
import com.raredev.objects.S3ObjectKeyParser;
import com.raredev.processors.CsvProcessor;
import com.raredev.processors.FileProcessor;
import com.raredev.processors.ProcessorFactory;


import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

public class ReportGeneratorHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        Instant startTime = Instant.now();
        try {
            String bucketName = System.getenv(Constants.BUCKET_NAME);
            String objectKey = Constants.EMPTY;

            CloudWatchLogManager.build(context.getLogger());

            //Received Event:
            String event = IOUtils.toString(inputStream);
            S3EventNotification s3Event = S3EventNotification.parseJson(event);

            //Initialize BucketName:
            if(bucketName==null) {
                bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
                CloudWatchLogManager.getInstance().log("BucketName initialized: "+bucketName);
            }

            //Initialize ObjectKey:
            if(objectKey.isEmpty()){
                objectKey = s3Event.getRecords().get(0).getS3().getObject().getKey();
                CloudWatchLogManager.getInstance().log("ObjectKey initialized: "+objectKey);
            }

            //Initialize AmazonServiceManager:
            AmazonServiceManager amazonServiceManager=new AmazonServiceManager(bucketName);
            TransformationConfigManager.initializeTransformations();
            amazonServiceManager.setAwsRequestId(context.getAwsRequestId());

            //Initialize S3ObjectKeyParser:
            S3ObjectKeyParser s3ObjectKeyParser = new S3ObjectKeyParser(objectKey);
            CloudWatchLogManager.getInstance().log("S3ObjectKeyParser: "+s3ObjectKeyParser.toString());

            //Fetched config from config path:
            String fileExtension=s3ObjectKeyParser.getFileExtension();
            String fileName=s3ObjectKeyParser.getFileNameWithoutExtension();
            String fileSuffix=fileName.substring(fileName.lastIndexOf("-")+1);
            String configFilePath = Constants.CONFIG_BASE+fileExtension+Constants.DASH+Constants.CONFIG+Constants.DASH+fileSuffix+Constants.JSON_EXT;
            InputStream configInputStream = amazonServiceManager.getS3Object(configFilePath).getObjectContent();
            String configString = convertToString(configInputStream);

            MasterConfigManager masterConfigManager = new MasterConfigManager(configString,objectKey);

            CloudWatchLogManager.getInstance().log("AwsRequestId: "+context.getAwsRequestId());
            CloudWatchLogManager.getInstance().log("Event Received: "+event);

            //Fetched inputFile from s3:
            InputStream s3ObjectInputStream = amazonServiceManager.getS3Object(objectKey).getObjectContent();
            CloudWatchLogManager.getInstance().log("S3Object Received: "+s3ObjectInputStream.toString());

            //processing inputFile generating outputFile data:
            try {
                FileProcessor fileProcessor = ProcessorFactory.getProcessor(fileExtension);
                fileProcessor.process(s3ObjectInputStream);
                masterConfigManager.generateOutputFileData();
            } catch(Exception e){
                 CloudWatchLogManager.getInstance().log("Exception occured while fetching processor");
                 e.printStackTrace();
            }

            //writing output file:
            String outputFileName=Constants.REPORT+Constants.DASH+fileSuffix+Constants.CSV_EXT;
            String reportFilePath = Constants.TMP_LAMBDA+outputFileName;
            CloudWatchLogManager.getInstance().log("Server OutputFilePath :"+reportFilePath);
            CsvProcessor.writeCsvFile(reportFilePath,
                    MasterConfigManager.getOutputColumnIndexMap(),
                    MasterConfigManager.getOutputFileData());

            // Uploading report to S3:
            CloudWatchLogManager.getInstance().log("===> Going to call uploadToS3 for outputFile");
            amazonServiceManager.uploadToS3( Constants.PROCESSED_PATH+outputFileName, new File(reportFilePath));
        } catch (Exception e) {
            CloudWatchLogManager.getInstance().log("Error processing file: " + e.getMessage());
        } finally {
            Instant endTime = Instant.now();
            CloudWatchLogManager.getInstance().log("Time Taken to complete process: "+processingTime(startTime,endTime));
            CloudWatchLogManager.getInstance().log("====== Processing Completed Successfully! ======");
        }
    }

    public String convertToString(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void printContent(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            CloudWatchLogManager.getInstance().log(line);
        }
    }

    private String processingTime(Instant startTime, Instant endTime){
        // Calculate the duration
        Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        return String.format(
                "%d hours, %d minutes, %d seconds",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
    }
}
