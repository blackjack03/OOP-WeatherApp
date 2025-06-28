package org.app.config;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h2>UserPreferences</h2>
 * <p>POJO che memorizza le preferenze persistenti dell’utente. Al momento la
 * sezione contiene solo l’ID della città predefinita, ma può essere estesa in
 * futuro (unità di misura, tema UI, frequenza refresh, ecc.).</p>
 *
 * <p>La classe è annotata con Jackson per una facile (de)serializzazione dal
 * file di configurazione JSON/YAML. Per evitare l’uso di <code>null</code> nel
 * resto del codice, l’accessor espone un {@link Optional Optional&lt;Integer&gt;}</p>.
 */
public class UserPreferences {

    /** Identificativo univoco della città predefinita scelto dall’utente. */
    private Integer defaultCity;

    /**
     * Ritorna l’ID della città memorizzato.
     *
     * @return {@link Optional#empty()} se l’utente non ha mai salvato alcuna
     *         città di default, altrimenti l’ID contenuto nell’<em>Optional</em>.
     */
    @JsonProperty
    public Optional<Integer> getDefaultCity() {
        return defaultCity == null ? Optional.empty() : Optional.of(defaultCity);
    }

    /**
     * Imposta o aggiorna l’ID della città predefinita. Può essere invocato
     * anche con <code>null</code> per rimuovere la preferenza.
     *
     * @param defaultCity ID della città come intero, oppure <code>null</code>.
     */
    @JsonProperty
    public void setDefaultCity(final Integer defaultCity) {
        this.defaultCity = defaultCity;
    }

}
