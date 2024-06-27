package com.raredev.processors;

import com.raredev.managers.CloudWatchLogManager;
import com.raredev.managers.MasterConfigManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CsvProcessor extends FileProcessor {
    @Override
    public void process(InputStream s3ObjectInputStream) {
        Map<Integer, String> headers = MasterConfigManager.getInputColumnIndexMap();
        List<String[]> data = MasterConfigManager.getInputFileData();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            // Extract headers
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
                headers.put(entry.getValue(), entry.getKey());
            }

            // Extract data
            for (CSVRecord csvRecord : csvParser) {
                String[] row = new String[headers.size()];
                for (int i = 0; i < headers.size(); i++) {
                    row[i] = csvRecord.get(i) != null ? csvRecord.get(i) : "";
                }
                data.add(row);
            }

        } catch (IOException e) {
            System.err.println("Error processing CSV file: " + e.getMessage());
        }

        // Print headers and data for demonstration purposes
        System.out.println("CsvProcessor:");
        System.out.println("Headers: " + headers);
        for (String[] row : data) {
            System.out.println(", "+ Arrays.toString(row));
        }
    }

    public static void writeCsvFile(String filePath, Map<Integer, String> headerMap, List<String[]> data) throws IOException {

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the header
            String header = String.join(",", headerMap.values());
            writer.write(header);
            writer.newLine();

            // Write the data
            for (String[] row : data) {
                String line = String.join(",", row);
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e){
            CloudWatchLogManager.getInstance().log("Exception occurred while writing outputFile: "+e.getMessage());
            e.printStackTrace();
        } finally {
            CloudWatchLogManager.getInstance().log("===> Writing outputFile completed Successfully");
        }
    }
}