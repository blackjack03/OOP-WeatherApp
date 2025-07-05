package org.app.weathermode.model;

import java.util.Optional;
import java.util.Map;

/**
 * <h2>Weather (contratto)</h2>
 * <p>Interfaccia che definisce le operazioni base di un provider meteo
 * all’interno dell’applicazione. Astrae la fonte dati (API REST, scraping,
 * cache locale…) consentendo di sostituire l’implementazione concreta senza
 * impattare il resto del codice.</p>
 */
public interface Weather {

    /**
     * Imposta la località su cui eseguire tutte le future richieste meteo.
     * L’implementazione dovrebbe effettuare eventuali reset di cache interna.
     *
     * @param locationInfo mappa con chiavi standard (es. "lat", "lng", "city").
     */
    void setLocation(Map<String, String> locationInfo);

    /**
     * Scarica in un’unica chiamata (o più, a seconda del provider) le previsioni
     * orarie e giornaliere, popolando la cache interna dell’implementazione.
     *
     * @return <code>true</code> se il download/parsing è riuscito.
     */
    boolean reqestsAllForecast();

    /**
     * @return previsioni orarie per i prossimi <em>N</em> giorni nel formato
     *         <code>Map&lt;ISO_DATE, Map&lt;HH, Map&lt;metrica,valore&gt;&gt;&gt;</code>.
     */
    Optional<Map<String, Map<String, Map<String, Number>>>> getAllForecast();

    /**
     * @return riepilogo giornaliero (icona, min/max temp, UV, ecc.) per i
     *         prossimi giorni: <code>Map&lt;ISO_DATE, Map&lt;metrica,valore&gt;&gt;</code>.
     */
    Optional<Map<String, Map<String, Number>>> getDailyGeneralForecast();

    /**
     * @return informazioni aggiuntive per giorno (alba, tramonto, ecc.)
     *         <code>Map&lt;ISO_DATE, Map&lt;metrica,stringa&gt;&gt;</code>.
     */
    Optional<Map<String, Map<String, String>>> getDailyInfo();

    /**
     * Previsione puntuale (precisione implementazione-specifica) per una data
     * e ora indicata.
     *
     * @param day   giorno del mese (1-31)
     * @param month mese (1-12)
     * @param year  anno (4 cifre)
     * @param hour  stringa "HH:mm"; l’implementazione gestisce eventuali
     *              arrotondamenti al quarto d’ora/ora.
     * @return un {@link Optional} con la mappa metrica→valore se disponibile.
     */
    Optional<Map<String, Number>> getWeatherOn(int day, int month, int year, String hour);

    /**
     * @return numero di giorni inclusi nell’ultima previsione scaricata.
     */
    int getForecastDays();

    /**
     * Condizioni meteo correnti con caching interno (timeout implementazione-specifico).
     *
     * @param avoidCheck se <code>true</code> forza il refresh ignorando la cache.
     * @return coppia { timestampISO, mapMetriche }.
     */
    Optional<Pair<String, Map<String, Number>>> getWeatherNow(boolean avoidCheck);

    /**
     * @return informazioni statiche sulla città (altitudine, abitanti, ecc.).
     */
    Optional<Map<String, Number>> getCityInfo();

    /**
     * Converte la direzione del vento espressa in gradi bussola in descrizione
     * testuale (es. "Nord-Est").
     *
     * @param degrees valore 0-359.
     * @return descrizione testuale della direzione del vento.
     */
    String getWindDirection(int degrees);

}
