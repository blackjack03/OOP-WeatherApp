package org.app.weathermode.model;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h2>ApiConfig</h2>
 * <p>Classe minimalista che incapsula la <strong>chiave API</strong> utilizzata
 * dall’applicazione per accedere a servizi esterni.
 * È annotato con <a href="https://github.com/FasterXML/jackson">Jackson</a>
 * così da poter essere serializzato/deserializzato automaticamente da/verso
 * YAML o JSON nel file di configurazione dell’utente.</p>
 * <p>La scelta di restituire un {@link Optional}&lt;String&gt; invece di un
 * <code>null</code> riduce il rischio di <em>NullPointerException</em> e rende
 * più esplicita la possibilità che la chiave non sia stata impostata.</p>
 */
public class ApiConfig {

    /** Chiave API in chiaro. <strong>Non dovrebbe</strong> essere loggata. */
    private String apiKey;

    /* ======================== Accessor ========================= */

    /**
     * Restituisce la chiave API, se presente.
     *
     * @return {@link Optional#empty()} se l’utente non ha definito alcuna chiave
     *         nel file di configurazione; in caso contrario un {@link Optional}
     *         contenente la stringa.
     */
    @JsonProperty
    public Optional<String> getApiKey() {
        return apiKey == null ? Optional.empty() : Optional.of(apiKey);
    }

    /**
     * Imposta la chiave API a runtime (ad es. dopo aver letto il file YAML).
     *
     * @param apiKey stringa esatta della chiave così come fornita dal servizio.
     */
    @JsonProperty
    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

}
