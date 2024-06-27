package com.raredev.processors;

import java.io.InputStream;

public abstract class FileProcessor {
    public abstract void process(InputStream s3ObjectInputStream);
}
