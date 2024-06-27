package com.raredev.managers;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Data
public class AmazonServiceManager {

    @Getter @Setter
    private AmazonS3 s3Client;
    @Getter @Setter
    private String bucketName;

    @Getter @Setter
    private String awsRequestId;

    public AmazonServiceManager(String bucketName) {
        this.bucketName=bucketName;
        this.s3Client= AmazonS3Client.builder().withCredentials(new DefaultAWSCredentialsProviderChain()).build();
//       String accessKey="";
////     String secretKey="";
//       this.s3Client = AmazonS3ClientBuilder.standard()
//                .withRegion(Regions.AP_SOUTH_1)
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey,secretKey)))
//                .build();
        CloudWatchLogManager.getInstance().log("======== AmazonServiceManager Initialized Success ========");
    }

    public S3Object getS3Object(String s3Path){
        S3Object s3Object=null;
        try {
            if(s3Client!=null) {
                return s3Client.getObject(bucketName,s3Path);
            }
        }catch (Exception e){
            CloudWatchLogManager.getInstance().log(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void uploadToS3(String s3Path, File f){
        CloudWatchLogManager.getInstance().log("===> Inside upload to S3 bucket");
        CloudWatchLogManager.getInstance().log("===> Lambda server File path : "+f.getAbsolutePath());
        CloudWatchLogManager.getInstance().log("===> S3 Dest File path : "+s3Path);
        try {
            if(s3Client!=null) {
                s3Client.putObject(bucketName,s3Path,f);
                deleteFile(f);
            }
        }catch (Exception e){
            CloudWatchLogManager.getInstance().log(e.getMessage());
            e.printStackTrace();
        } finally {
            CloudWatchLogManager.getInstance().log("===> Uploaded file to s3 processed/ folder successfully");
        }
    }

    private void deleteFile(File file){
        try {
            Files.deleteIfExists(file.toPath());
            CloudWatchLogManager.getInstance().log("File deleted successfully at Lambda server path: "+file.getPath());
        } catch(IOException e){
             CloudWatchLogManager.getInstance().log("Unable to delete file at: "+file.getPath());
        }
    }
}
