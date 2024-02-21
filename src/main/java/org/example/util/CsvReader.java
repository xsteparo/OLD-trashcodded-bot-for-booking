package org.example.util;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.util.ConfigLoader.getPathResources;

@NoArgsConstructor
public class CsvReader {
    private final Logger consoleLogger = LogManager.getLogger(CsvReader.class);

    public List<String[]> extractAppointmentsData(String packagePath, String csvPath) {
        return extractData(packagePath, csvPath, ",");
    }

    public List<String[]> extractProxies(String packagePath, String file) {
        return extractData(packagePath, file, ":");
    }

    private List<String[]> extractData(String packagePath, String fileName, String delimiter) {
        List<String[]> data = new ArrayList<>();
        String csvFile = "%s/%s/%s".formatted(getPathResources(), packagePath, fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowData = line.split(delimiter);
                data.add(rowData);
            }
            consoleLogger.info("Успешное чтение данных из файла: {}", csvFile);
        } catch (IOException e) {
            consoleLogger.error("Ошибка при чтении данных из файла: {}", csvFile);
        }
        return data;
    }
}

