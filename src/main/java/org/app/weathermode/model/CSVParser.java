package org.app.weathermode.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;

public interface CSVParser {

    List<Map<String, String>> readCSVToMap() throws IOException, CsvException;

    List<String> getHeader() throws IOException, CsvException;

}
