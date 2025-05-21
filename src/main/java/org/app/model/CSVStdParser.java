package org.app.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVStdParser extends CSVReader implements CSVParser {

    public CSVStdParser(final String csvFilePath) throws FileNotFoundException {
        super(new FileReader(csvFilePath));
    }

    @Override
    public List<Map<String, String>> readCSVToMap() throws IOException, CsvException {
        final List<Map<String, String>> resultList = new ArrayList<>();
        final List<String[]> allRows = this.readAll();

        if (allRows.isEmpty()) {
            return resultList;
        }

        final String[] header = allRows.get(0);

        for (int i = 1; i < allRows.size(); i++) {
            final String[] row = allRows.get(i);
            final Map<String, String> rowMap = new HashMap<>();

            for (int j = 0; j < header.length; j++) {
                rowMap.put(header[j], row[j]);
            }

            resultList.add(rowMap);
        }

        return resultList;
    }

}
