package org.app.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URISyntaxException;
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
    public static void createConfigIfNotExists(final String configPath)
            throws URISyntaxException, IOException {

        System.out.println("Checking configuration...");

        final String configFolderName = "app_config";
        checkAndCreateConfigFolder(configFolderName);

        final File file = new File(configPath);

        // 2️⃣ crea la cartella padre se manca
        final File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        if (file.exists()) {
            System.out.println("Il file esiste già: " + file.getAbsolutePath());
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(DEFAULT_CONFIG);
            System.out.println("File creato: " + file.getAbsolutePath());
        }
    }

    private static void checkAndCreateConfigFolder(final String folderName)
            throws URISyntaxException, IOException {

        // cartella dove risiede il JAR (o target/classes in IDE)
        final Path jarFile = Paths.get(
                ConfigBuilder.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
        );
        final Path jarDir = Files.isRegularFile(jarFile) ? jarFile.getParent() : jarFile;

        // 1️⃣ usa davvero il parametro folderName
        final Path targetDir = jarDir.resolve(folderName);

        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
            System.out.println("Cartella creata: " + targetDir.toAbsolutePath());
        } else {
            System.out.println("Cartella già presente: " + targetDir.toAbsolutePath());
        }
    }

}
