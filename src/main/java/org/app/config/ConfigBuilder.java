package org.app.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import java.util.logging.Logger;

/**
 * <h2>ConfigBuilder</h2>
 * <p>Utility ad‑hoc che si occupa di <strong>creare</strong> il file di
 * configurazione qualora non esista ancora sul filesystem.</p>
 * <p>L’implementazione è volutamente semplice: una volta verificata
 * l’esistenza, se il file manca viene scritto un template JSON minimale in
 * modo da consentire all’utente di personalizzarlo in un secondo momento.</p>
 */
public final class ConfigBuilder {

    private static final Logger LOG = Logger.getLogger(ConfigManager.class.getName());

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

    /** Impedisce l’instanziazione accidentale. */
    private ConfigBuilder() { }

    /**
     * Verifica la presenza del file di configurazione e, se assente, lo crea
     * popolandolo con {@link #DEFAULT_CONFIG}.
     *
     * @param configPath percorso assoluto o relativo del file da controllare.
     */
    public static void createConfigIfNotExists(final String configPath)
            throws URISyntaxException, IOException {

        LOG.fine("Checking configuration...");

        final String configFolderName = "app_config";
        checkAndCreateConfigFolder(configFolderName);

        final File file = new File(configPath);

        final File parent = file.getParentFile();
        // Creo la cartella padre se non esiste
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create folder: " + parent.getAbsolutePath());
        }

        if (file.exists()) {
            LOG.fine("Il file esiste già: " + file.getAbsolutePath());
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                file.toPath(),
                StandardCharsets.UTF_8
        )) {
            writer.write(DEFAULT_CONFIG);
            LOG.fine("File creato: " + file.getAbsolutePath());
        }
    }

    private static void checkAndCreateConfigFolder(final String folderName)
            throws URISyntaxException, IOException {

        final Path jarFile = Paths.get(
                ConfigBuilder.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
        );
        final Path jarDir;
        if (Files.isRegularFile(jarFile)) {
            final Path parent = jarFile.getParent();
            jarDir = (parent != null) ? parent : jarFile;
        } else {
            jarDir = jarFile;
        }

        final Path targetDir = jarDir.resolve(folderName);

        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
            LOG.fine("Cartella creata: " + targetDir.toAbsolutePath());
        } else {
            LOG.fine("Cartella già presente: " + targetDir.toAbsolutePath());
        }
    }

}
