package org.app.model;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigManager {
    private static AppConfig config;
    
    public static void loadConfig(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(new File(filePath), AppConfig.class);
            System.out.println ("Configuration loaded successfully.");
        } catch (IOException e) {
            throw new RuntimeException ("ERROR! File cannot be loaded.");
        }
    }

    public static AppConfig getConfig() {
        if (config == null) {
            throw new IllegalStateException ("Configuration not loaded; call loadConfig first. ");
        }
        return config;
    }

    public static void saveConfig(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
            System.out.println ("Configuration saved successfully.");
        } catch (IOException e) {
            throw new RuntimeException ("ERROR! Save unsuccessful");
        }
    }

    /*
    // Esempio di utilizzo
    public static void main(String[] args) {
        loadConfig("src/main/java/org/files/config.json");
        AppConfig appConfig = getConfig();

        System.out.println("API URL: " + appConfig.getApi().getBaseUrl() + "API KEY: " + appConfig.getApi().getApiKey());
        System.out.println("Default City: " + appConfig.getUserPreferences().getDefaultCuty());
        System.out.println("Defalut Units: " + appConfig.getUserPreferences().getUnits());
        System.out.println("Default Language: " + appConfig.getUserPreferences().getLanguage());
    }
    */
}
