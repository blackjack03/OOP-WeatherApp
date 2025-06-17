package org.app.model;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class ConfigManager {
    private static AppConfig config;
    private static final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new Jdk8Module())       // supporto a Optional
        .findAndRegisterModules();

    public static void loadConfig(String filePath) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(new File(filePath), AppConfig.class);
            System.out.println("Configuration loaded successfully.");
        } catch (final IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("ERROR! File cannot be loaded.");
        }
    }

    public static AppConfig getConfig() {
        if (config == null) {
            throw new IllegalStateException("Configuration not loaded; call loadConfig first. ");
        }
        return config;
    }

    public static void saveConfig(String filePath) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
            System.out.println("Configuration saved successfully.");
        } catch (final IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("ERROR! Save unsuccessful");
        }
    }

    // Esempio di utilizzo
    public static void main(String[] args) {
        loadConfig("src/main/java/org/files/configuration.json");
        final AppConfig appConfig = getConfig();

        if (appConfig.getUserPreferences().getDefaultCity().isEmpty()) {
            System.out.println("Default city is not set.");
        } else {
            System.out.println("Default city: " + appConfig.getUserPreferences().getDefaultCity().get());
        }

        /*System.out.println("API URL: " + appConfig.getApi().getBaseUrl() + "API KEY: " + appConfig.getApi().getApiKey());
        System.out.println("Default City: " + appConfig.getUserPreferences().getDefaultCity());
        System.out.println("Defalut Units: " + appConfig.getUserPreferences().getUnits());
        System.out.println("Default Language: " + appConfig.getUserPreferences().getLanguage());*/
    }

}
