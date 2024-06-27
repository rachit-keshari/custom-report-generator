package com.raredev.managers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CloudWatchLogManager {

    private static CloudWatchLogManager instance;
    private final LambdaLogger lambdaLogger;
    private static final Object MUTEX = new Object();
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudWatchLogManager.class);

    public CloudWatchLogManager(LambdaLogger lambdaLogger) {
        if(lambdaLogger==null) {
            this.lambdaLogger = new LambdaLogger() {
                @Override
                public void log(String s) {
                    LOGGER.info(s);
                }

                @Override
                public void log(byte[] bytes) {
                     LOGGER.info("{0}",bytes);
                }
            };
        }else{
            this.lambdaLogger=lambdaLogger;
        }
    }

    public static void build(LambdaLogger logger){
        synchronized (MUTEX) {
            if(instance == null){
                instance = new CloudWatchLogManager(logger);
            }
        }
    }

    public static void log(String message){ getInstance().log(message); }

    public static LambdaLogger getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CloudwatchLogManager not initialized yet");
        }
        return instance.lambdaLogger;
    }

    public static boolean hasInstance(){ return instance != null; }
}
