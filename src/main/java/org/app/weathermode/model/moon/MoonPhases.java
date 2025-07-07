package org.app.weathermode.model.moon;

import java.util.Optional;
import java.util.Map;

/**
 * <h2>MoonPhases</h2>
 * <p>Contratto funzionale per un provider di informazioni sulle <strong>
 * fasi lunari</strong>.
 */
public interface MoonPhases {

    /**
     * Imposta la data target su cui calcolare/recuperare la fase lunare.
     *
     * @param year  anno (4 cifre).
     * @param month mese (1‑12).
     * @param day   giorno (1‑31).
     */
    void setDate(int year, int month, int day);

    /**
     * Restituisce una mappa con le informazioni principali relative alla luna
     * per la data corrente, ad esempio:
     * <ul>
     *   <li><code>date</code> – data formattata</li>
     *   <li><code>state</code> – fase ("Luna piena", "Primo quarto", …)</li>
     *   <li><code>illumination</code> – percentuale di disco illuminato</li>
     *   <li><code>image_name</code> – nome file PNG/JPG dell’immagine</li>
     * </ul>
     * L’implementazione può aggiungere ulteriori chiavi se necessario.
     *
     * @return <code>Optional.empty()</code> in caso di errore (rete, parsing).
     */
    Optional<Map<String, String>> getMoonInfo();

    /**
     * @return la data attualmente impostata, formato e semantica definiti
     *         dall’implementazione (tipicamente <code>dd/MM/yyyy</code>).
     */
    String getDate();

    /**
     * Utility di convenienza per ottenere l’URL completo dell’immagine
     * rappresentativa della fase lunare.
     *
     * @param imageName nome file dell’immagine.
     * @return URL assoluto.
     */
    String getImageURL(String imageName);

}
