package com.raredev.processors;

import com.raredev.managers.CloudWatchLogManager;
import java.util.function.Supplier;

public enum ProcessorFactory {
    CSV(CsvProcessor::new), XLSX(XlsxProcessor::new);

    private final Supplier<FileProcessor> processor;
    ProcessorFactory(Supplier<FileProcessor> processor) {
      this.processor=processor;
    }

    public static FileProcessor getProcessor(String processorType){
        switch (processorType.toUpperCase()){
            case "CSV":
                return CSV.processor.get();
            case "XLSX":
                return XLSX.processor.get();
            default:
                CloudWatchLogManager.log("Exception occured: Icorrect file Format Received");
                throw new RuntimeException("ERROR: file Processor is not matching, only CSV & XLSX allowed");
        }
    }
}
