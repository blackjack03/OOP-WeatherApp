package org.app.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <h2>ConfigBuilder</h2>
 * <p>Utility ad‑hoc che si occupa di <strong>creare</strong> il file di
 * configurazione qualora non esista ancora sul filesystem.</p>
 * <p>L’implementazione è volutamente semplice: una volta verificata
 * l’esistenza, se il file manca viene scritto un template JSON minimale in
 * modo da consentire all’utente di personalizzarlo in un secondo momento.</p>
 */
public class ConfigBuilder {

    /**
     * Contenuto JSON di default scritto nel nuovo file.
     * <pre>{
     *   "api" : { "apiKey": null },
     *   "userPreferences": { "defaultCity": null }
     * }</pre>
     */
    private static final String DEFAULT_CONFIG = """
{
  "api": {
    "apiKey": null
  },
  "userPreferences": {
    "defaultCity": null
  }
}
""".trim();

    /**
     * Verifica la presenza del file di configurazione e, se assente, lo crea
     * popolandolo con {@link #DEFAULT_CONFIG}.
     *
     * @param configPath percorso assoluto o relativo del file da controllare.
     */
    public static void createConfigIfNotExists(final String configPath) throws IOException {
        System.out.println("Checking configuration...");
        final File file = new File(configPath);
        if (file.exists()) {
            System.out.println("Il file esiste.");
            return;
        }
        final FileWriter writer = new FileWriter(file);
        writer.write(DEFAULT_CONFIG);
        System.out.println("File creato con contenuto di default.");
        writer.close();
    }

}
