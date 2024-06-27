package com.raredev.constants;

import java.util.regex.Pattern;

public class Constants {
    public static final String BUCKET_NAME = "bucket-name";
    public static final String EMPTY = "";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String TRUE = "true";
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final String OPEN_CURLY_BRACES = "{";
    public static final String FALSE = "false";
    public static final String S3_EVENT = "aws:s3";
    public static final String CREATE_NEW_DATE = "CreateNewDate";
    public static final Pattern DIGIT_PATTERN = Pattern.compile("[1-9]");
    public static final String PIPE_SPLITTER = "|";
    public static final String COLON = ":";
    public static final String IF_ELSE = "IfElse";
    public static final String FORMAT_DATE = "FormatDate";
    public static final String CSV = "csv";
    public static final String CONFIG_BASE = "config/";
    public static final String JSON_EXT = ".json";
    public static final String DASH = "-";
    public static final String TMP_PATH = "tmp/";
    public static final String SLASH = "/";
    public static final String REPORT = "report";
    public static final String CSV_EXT = ".csv";
    public static final String PROCESSED_PATH = "processed/";
    public static final String CONFIG = "config";
    public static final String UNPROCESSED_BASE = "unprocessed/";

    private Constants() {
    }
}
