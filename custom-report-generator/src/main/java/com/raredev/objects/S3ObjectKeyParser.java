package com.raredev.objects;

import com.raredev.constants.Constants;
import com.raredev.managers.CloudWatchLogManager;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.util.regex.Pattern;

@Data
public class S3ObjectKeyParser {
    @Getter
    private final String objectKey;
    @Getter
    private final String fileExtension;
    @Getter
    private final String fileNameWithExtension;
    @Getter
    private final String fileNameWithoutExtension;
    @Getter
    private final String folderPath;

    public S3ObjectKeyParser(String objectKey) {

        String[] splitKey = objectKey.split(Constants.SLASH);
        this.objectKey = objectKey;
        this.fileNameWithExtension = splitKey[splitKey.length-1];
        this.fileExtension = fileNameWithExtension.substring(fileNameWithExtension.lastIndexOf(".")+1).toLowerCase();
        this.fileNameWithoutExtension=fileNameWithExtension.split(Constants.DOUBLE_BACKSLASH+Constants.DOT)[0];
        this.folderPath = objectKey.substring(0,objectKey.lastIndexOf(Constants.SLASH)+1);

        CloudWatchLogManager.getInstance().log("====== S3ObjectKeyParser Initialized Success ======");
    }

    @Override
    public String toString() {
        return "S3ObjectKeyParser{" +
                "objectKey='" + objectKey + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", fileNameWithExtension='" + fileNameWithExtension + '\'' +
                ", fileNameWithoutExtension='" + fileNameWithoutExtension + '\'' +
                ", folderPath='" + folderPath + '\'' +
                '}';
    }
}
