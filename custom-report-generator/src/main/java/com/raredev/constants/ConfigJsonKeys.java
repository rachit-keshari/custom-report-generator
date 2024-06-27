package com.raredev.constants;

public final class ConfigJsonKeys {
    public static final String BATCH_SIZE_IN_BYTES = "batch_size_in_bytes";
    public static final String FAILED_ROWS_CONFIGURATION = "failed_rows_configuration";
    public static final String OUTPUT_LOCATIONS = "output_locations";
    public static final String INPUT_FILE_DELIMITER = "input_file_delimiter";
    public static final String OUTPUT_CONFIGURATION = "output_configuration";
    public static final String SNS_TOPIC = "sns_topic";
    public static final String SQS_TOPIC = "sqs_topic";

    public static final String KAFKA_TOPIC = "kafka_topic";
    public static final String RABBITMQ_NAME = "rabbitmq_name";
    public static final String ADDITIONAL_COLUMNS = "additional_columns";
    public static final String TRANSFORM = "transform";
    public static final String NAME = "name";
    public static final String HARDCODED_COLUMNS = "hardcoded_columns";
    public static final String VALUE = "value";
    public static final String FILE_NAME_PREFIX = "file_name_prefix";
    public static final String OUTPUT_FORMAT = "output_format";
    public static final String RENAME_TO = "rename_to";
    public static final String INCLUDE_ROW_CRITERIA = "include_row_criteria";
    public static final String INPUT_COLUMNS = "input_columns";
    public static final String COLUMN_ORDER = "column_order";
    public static final String ALLOWED_EXTENSIONS = "allowed_extensions";
    public static final String INPUT_CONFIGURATION = "input_configuration";
    public static final String INPUT_COLUMN_MAPPER = "input_column_mapper";
    public static final String OUTPUT_DELIMITER = "output_delimiter";
    public static final String OUTPUT_FOLDER = "output_folder";
    public static final String OUTPUT_BUCKET = "output_bucket";
    public static final String KAFKA_CONFIG = "kafka_config";
    public static final String RABBITMQ_CONFIG = "rabbitmq_config";
    public static final String EMAIL_NOTIFICATION = "email_notification";
    public static final String EXTENDS = "extends";
    public static final String INPUT_COLUMN_HEADS_START = "input_column_heads_start";
    public static final String RENAME_CONFIGURATION = "rename_configuration";
    public static final String IF_SHEET_NAME_CONTAINS = "if_sheet_name_contains";
    public static final String IGNORE_FIRST_ROW = "ignore_first_row_if_fails";
    public static final String IGNORE_LAST_ROW = "ignore_last_row_if_fails";
    public static final String PASSWORD_TO_UNZIP = "password_to_unzip";
    public static final String FORWARDED_PROCESSING_TO_SQS = "forward_processing_to_sqs";
    public static final String RENAME_EXTENSION_ONLY = "rename_extension_only";
    public static final String PREPROCESSING_OPERATIONS = "preprocessing_operations";
    public static final String OPERATION = "operation";
    public static final String PARAMS = "params";
    public static final String CHANNEL = "channel";
    public static final String DAY_WINDOW = "day_window";
    public static final String DAY_WINDOW_RANGE = "day_window_range";
    public static final String OUTPUT_ROOT_DIR = "output_root_directory";
    public static final String MANDATORY_FIELDS = "mandatory_fields";
    public static final String PARTIAL_FILE_ALLOWED = "partial_file_allowed";
    public static final String FORWARD_DECRYPTED_FILE = "forward_decrypted_file";
    public static final String FORWARD_EMAIL_IDS = "forward_email_ids";
    public static final String FILTER_FILES_FROM_ZIP = "filter_zip_files";
    public static final String CONFIG_TYPE = "config_type";
    public static final String EMPTY_FILE_ALLOWED = "empty_file_allowed";
    public static final String CREATE_SUCCESS_FILE = "create_success_file";
    public static final String ZIP_OUTPUT_CONFIG = "zip_output_config";
    public static final String FILE_NAME = "file_name";
    public static final String MERGE_REQUIRED = "merge_required";
    public static final String ENVIRONMENT = "env";
    public static final String INPUT_FILE_EXTENSION = "input_file_extension";
    public static final String OUTPUT_FILE_HEADERS = "output_file_headers";
    public static final String OUTPUT_FILE_CONFIG = "output_file_config";


    private ConfigJsonKeys() {
    }
}
