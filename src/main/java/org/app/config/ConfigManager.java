package org.app.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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

    private static final Logger LOG = Logger.getLogger(ConfigManager.class.getName());

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
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module())
        .findAndRegisterModules();

    /** Impedisce l’instanziazione accidentale. */
    private ConfigManager() { }

    /**
     * Carica il file di configurazione nel contesto statico.
     * <p>Viene usato un nuovo <code>ObjectMapper</code> “fresco” per evitare
     * side‑effect di configurazioni custom sull’istanza statica
     * {@link #MAPPER} destinata al salvataggio.</p>
     *
     * @param filePath percorso del file JSON/YAML da leggere.
     * @throws RuntimeException se il file non è leggibile o il parsing fallisce.
     */
    @SuppressFBWarnings(
        value = "THROWS_METHOD_THROWS_RUNTIMEEXCEPTION",
        justification = "Intentionally signalling fatal error to be handled at a higher level"
    )
    public static void loadConfig(final String filePath) {
        final ObjectMapper localMapper = new ObjectMapper().findAndRegisterModules();
        try {
            config = localMapper.readValue(new File(filePath), AppConfig.class);
            LOG.fine("Configuration loaded successfully.");
        } catch (final IOException e) { // NOPMD suppressed as it is a false positive
            throw new RuntimeException("ERROR! File cannot be loaded.", e); // NOPMD
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
    @SuppressFBWarnings(
        value = "THROWS_METHOD_THROWS_RUNTIMEEXCEPTION",
        justification = "Intentionally signalling fatal error to be handled at a higher level"
    )
    public static void saveConfig(final String filePath) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
            LOG.fine("Configuration saved successfully.");
        } catch (final IOException e) { // NOPMD suppressed as it is a false positive
            throw new RuntimeException("ERROR! Save unsuccessful", e); // NOPMD
        }
    }

}
