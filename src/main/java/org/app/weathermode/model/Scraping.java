package org.app.weathermode.model;

import java.util.*;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>Scraping (utility demo)</h2>
 * <p>Classe <em>stand‑alone</em> ad uso dimostrativo che mostra come effettuare
 * lo scraping della pagina meteo di <code>ilmeteo.it</code> per ricavare il
 * numero di abitanti di una località. Non è usata nel runtime principale
 * dell’applicazione ma funge da esempio/manual test.</p>
 *
 * <p>Il flusso nel <code>main</code> è il seguente:</p>
 * <ol>
 *   <li>efettua la GET sulla pagina HTML;</li>
 *   <li>isola l’elemento <code>.infoloc</code> con Jsoup;</li>
 *   <li>estrae, tramite {@link #getInhabitantsFromText(String)}, il numero di
 *       abitanti usando un’espressione regolare tollerante (<code>[\d.]</code>
 *       per gestire i separatori delle migliaia).</li>
 * </ol>
 * In produzione questa logica è incapsulata in
 * {@link AllWeather#getCityInhabitants(String)}; qui viene mantenuta a scopo di
 * debugging rapido.
 */
public class Scraping {

    /**
     * Entry‑point di test: effettua scraping sincrono e stampa su <em>stdout</em>
     * il numero di abitanti, se disponibile.
     */
    public static void main(String[] args) throws Exception {
        // final String URL = "https://www.3bmeteo.com/meteo/rovello%20porro";
        final String URL = "https://www.ilmeteo.it/meteo/New York";

        final Document doc = Jsoup.connect(URL).get();
        final Elements rawInfo = doc.getElementsByClass("infoloc");

        if (!rawInfo.isEmpty()) {
            final Element info = rawInfo.first();
            final Optional<Integer> inhabitants = getInhabitantsFromText(info.text());
            System.out.println("Abitanti: " + inhabitants.orElse(null));
        } else {
            System.out.println("Non disponibile!");
        }
    }

    /* ===================== helper privati ==================== */

    /**
     * Estrae con regex il numero di abitanti da una stringa (es. "12.345 abitanti").
     * @param rawText testo da cui estrarre.
     * @return <code>Optional.empty()</code> se la regex non trova corrispondenze.
     */
    private static Optional<Integer> getInhabitantsFromText(final String rawText) {
        final Matcher matcher = Pattern.compile("([\\d.]+)\\s*abitanti").matcher(rawText);
        return matcher.find() ? Optional.of(strToInt(matcher.group(1))) : Optional.empty();
    }

    /** Rimuove caratteri non numerici e converte in <code>int</code>. */
    private static int strToInt(final String str) {
        return Integer.parseInt(str.replaceAll("[^\\d]", ""));
    }

}
