package org.app.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <h2>AppConfig</h2>
 * <p>Root‑object del file di configurazione dell’applicazione. Riflette la
 * struttura JSON/YAML su disco e viene popolato automaticamente dalla libreria
 * di parsing (p. es. Jackson).</p>
 *
 * <p>Contiene due sezioni principali:</p>
 * <ul>
 *   <li>{@link #api} – impostazioni relative alle chiavi di accesso per i
 *       servizi esterni.</li>
 *   <li>{@link #userPreferences} – preferenze memorizzate lato utente (città
 *       di default, unità di misura, ecc.).</li>
 * </ul>
 * Tutti i campi sono pacchetto‑privati per permettere alla libreria di
 * serializzazione di accedere tramite <em>reflection</em>; i consumer del
 * modello devono passare per i metodi <code>get*</code> pubblici.
 */
@SuppressFBWarnings(
    value = {
        "UWF_UNWRITTEN_FIELD",
        "EI_EXPOSE_REP"},
    justification = "Field populated via reflection by Jackson"
    + ", and Intentional exposure of private fields"
)
public class AppConfig {
    /** Sotto‑sezione “api” del config file. */
    private ApiConfig api;
    /** Preferenze salvate dall’utente. */
    private UserPreferences userPreferences;

    /** @return configurazione API; mai <code>null</code> dopo la deserializzazione. */
    public ApiConfig getApi() {
        return api;
    }

    /** @return preferenze dell’utente corrente. */
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

}
