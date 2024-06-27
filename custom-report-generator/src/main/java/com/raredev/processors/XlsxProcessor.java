package com.raredev.processors;

import com.raredev.managers.MasterConfigManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class XlsxProcessor extends FileProcessor {
    @Override
    public void process(InputStream s3ObjectInputStream) {
        Map<Integer, String> headers = MasterConfigManager.getInputColumnIndexMap();
        List<String[]> data = MasterConfigManager.getInputFileData();

        try (Workbook workbook = new XSSFWorkbook(s3ObjectInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Process header row
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                for (Cell cell : headerRow) {
                    headers.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }

            // Process data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String[] rowData = new String[headers.size()];

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData[i] = getCellValueAsString(cell);
                }

                data.add(rowData);
            }
        } catch (IOException e) {
            System.err.println("Error processing XLSX file: " + e.getMessage());
        }

        // Print headers and data for demonstration purposes
        System.out.println("XlsxProcessor:");
        System.out.println("Headers: " + headers);
        for (String[] row : data) {
            System.out.println(", "+ Arrays.toString(row));
        }
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }
}

