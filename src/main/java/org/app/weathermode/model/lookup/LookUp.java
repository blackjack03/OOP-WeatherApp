package org.app.weathermode.model.lookup;

import org.app.weathermode.model.pair.Pair;

import java.util.Optional;

/* ===================================================================== */

/**
 * <h2>LookUp</h2>
 * <p>Interfaccia per i servizi di <strong>geolocalizzazione</strong> lato
 * client (es. <em>IP‑based</em>, GPS, ecc.). Fornisce un set di accessor per
 * i dati ottenuti e un metodo di inizializzazione <em>lazy</em>.
 * In caso la lookup fallisca, gli accessor devono restituire stringhe vuote o
 * <code>null</code>/default appropriati, a discrezione dell’implementazione.</p>
 */
public interface LookUp {

    /**
     * Effettua la procedura di lookup (rete, GPS, ecc.).
     *
     * @return <code>Optional.of(true)</code> se il lookup ha avuto successo,
     *         <code>Optional.empty()</code> altrimenti. Il valore <code>false</code>
     *         non è previsto, lasciando la segnalazione di fallimento al solo
     *         <em>empty</em>.
     */
    Optional<Boolean> lookup();

    /** @return indirizzo IP pubblico rilevato. */
    String getIP();
    /** @return codice ISO‑2 del Paese (es. "IT", "US"). */
    String getCountryCode();
    /** @return nome del Paese. */
    String getCountry();
    /** @return regione/amministrazione di primo livello, se disponibile. */
    String getRegion();
    /** @return città o località principale. */
    String getCity();
    /** @return codice postale. */
    String getZipCode();
    /** @return stringa timezone IANA (es. "Europe/Rome"). */
    String getTimeZone();

    /**
     * @return coppia latitudine/longitudine in gradi decimali. Può tornare
     *         <code>null</code> o coordinate (0,0) se non disponibili.
     */
    Pair<Double, Double> getCoords();

}
