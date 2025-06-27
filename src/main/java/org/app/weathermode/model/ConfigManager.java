package org.app.weathermode.model;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * <h2>ConfigManager</h2>
 * <p>Singleton <em>utility class</em> responsabile del <strong>lifecycle</strong>
 * del file di configurazione dell’applicazione:</p>
 * <ul>
 *   <li><strong>load</strong> in fase di boot tramite {@link #loadConfig(String)};</li>
 *   <li><strong>accesso</strong> centralizzato all’oggetto {@link AppConfig}
 *       con {@link #getConfig()};</li>
 *   <li><strong>persistenza</strong> su disco con {@link #saveConfig(String)}.</li>
 * </ul>
 * La classe non è istanziabile e sfrutta membri <code>static</code> per essere
 * disponibile ovunque senza passare riferimenti.
 */
public final class ConfigManager {

    /** Istanza globale della configurazione. Popolata da {@link #loadConfig(String)}. */
    private static AppConfig config;

    /**
     * <p>Singleton di {@link ObjectMapper} predisposto al supporto di:</p>
     * <ul>
     *   <li>{@link Jdk8Module} – serializzazione/deserializzazione di
     *       {@link java.util.Optional};</li>
     *   <li><code>findAndRegisterModules()</code> – auto-registrazione di moduli
     *       presenti sul classpath (Java Time, Kotlin, etc.).</li>
     * </ul>
     */
    private static final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new Jdk8Module())
        .findAndRegisterModules();

    /* ======================== lifecycle ======================== */

    /**
     * Carica il file di configurazione nel contesto statico.
     * <p>Viene usato un nuovo <code>ObjectMapper</code> “fresco” per evitare
     * side‑effect di configurazioni custom sull’istanza statica
     * {@link #mapper} destinata al salvataggio.</p>
     *
     * @param filePath percorso del file JSON/YAML da leggere.
     * @throws RuntimeException se il file non è leggibile o il parsing fallisce.
     */
    public static void loadConfig(String filePath) {
        final ObjectMapper localMapper = new ObjectMapper().findAndRegisterModules();
        try {
            config = localMapper.readValue(new File(filePath), AppConfig.class);
            System.out.println("Configuration loaded successfully.");
        } catch (final IOException e) {
            throw new RuntimeException("ERROR! File cannot be loaded.", e);
        }
    }

    /**
     * Restituisce l’oggetto {@link AppConfig} già deserializzato.
     *
     * @return configurazione corrente.
     * @throws IllegalStateException se <em>loadConfig</em> non è stato invocato
     *                               prima dell’accesso.
     */
    public static AppConfig getConfig() {
        if (config == null) {
            throw new IllegalStateException("Configuration not loaded; call loadConfig first.");
        }
        return config;
    }

    /**
     * Serializza l’oggetto {@link #config} sul percorso specificato con
     * formattazione “pretty‑print”.
     *
     * @param filePath destinazione del file.
     * @throws RuntimeException se si verifica un errore di I/O.
     */
    public static void saveConfig(String filePath) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
            System.out.println("Configuration saved successfully.");
        } catch (final IOException e) {
            throw new RuntimeException("ERROR! Save unsuccessful", e);
        }
    }

    /* ===================== costruttore privato ==================== */
    /** Impedisce l’instanziazione accidentale. */
    private ConfigManager() {}
}
