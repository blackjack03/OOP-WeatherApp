package org.app.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigBuilder {

    private static final String DEFAULT_CONFIG = """
{
  "api" : {
    "apiKey": null
  },
  "userPreferences": {
    "defaultCity": null
  }
}
""".trim();

    public static void createConfigIfNotExists(final String configPath) {
        System.out.println("Checking configuration...");
        final File file = new File(configPath);
        if (file.exists()) {
            System.out.println("Il file esiste.");
        } else {
            try (final FileWriter writer = new FileWriter(file)) {
                writer.write(DEFAULT_CONFIG);
                System.out.println("File creato con contenuto di default.");
            } catch (final IOException e) {
                System.err.println("Errore nella creazione del file: " + e.getMessage());
            }
        }
    }

}
