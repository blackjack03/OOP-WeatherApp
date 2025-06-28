package org.app.weathermode.model;

import org.jsoup.*;
import org.jsoup.nodes.*;

import java.util.*;
import java.time.LocalDate;

/**
 * <h2>MoonPhasesImpl</h2>
 * <p>Implementazione di {@link MoonPhases} che effettua <strong>scraping</strong>
 * del sito <a href="https://www.moongiant.com">MoonGiant</a> (versione in
 * italiano) per estrarre fase lunare, percentuale di illuminazione e immagine
 * rappresentativa per una determinata data.</p>
 *
 * <p>La classe offre:</p>
 * <ul>
 *   <li>Impostazione della data tramite {@link #setDate(int, int, int)} (con
 *       formattazione <code>dd/MM/yyyy</code> richiesta dal sito).</li>
 *   <li>Recupero delle informazioni in {@link #getMoonInfo()} con caching in
 *       memoria per evitare richieste ripetute alla stessa data.</li>
 *   <li>Utility per ottenere l’URL completo dell’immagine fase lunare via
 *       {@link #getImageURL(String)}.</li>
 * </ul>
 * <p><strong>Nota:</strong> lo scraping HTML è fragile per definizione; un
 * cambio di layout sul sito sorgente potrebbe invalidare i <em>selector</em>.
 * Per questo motivo il metodo privato {@link #retrieveMoonPhaseInfo()} è
 * incapsulato in un blocco <code>try/catch</code> e restituisce <code>false</code>
 * in caso di errore, lasciando al chiamante la decisione su come gestirlo.</p>
 */
public class MoonPhasesImpl implements MoonPhases {

    private static final String SITE_URL = "https://www.moongiant.com/it/fase-lunare/";
    private static final String BASE_IMG_URL = "https://www.moongiant.com/images/today_phase/";

    /** Mappa con le info relative all’ultima data richiesta. */
    private final Map<String, String> MOON_INFO = new HashMap<>();
    /** Cache per evitare ripetuti scraping della stessa data. */
    private final Map<String, Map<String, String>> CACHE = new HashMap<>();
    /** Data corrente in formato <code>dd/MM/yyyy</code>. */
    private String date = "";

    /** Costruttore di default: la data verrà impostata a <em>oggi</em> alla prima richiesta. */
    public MoonPhasesImpl() { /* empty constructor */ }

    /**
     * Costruttore con data iniziale.
     * @param year  anno (es. 2025).
     * @param month mese (1–12).
     * @param day   giorno del mese (1–31).
     */
    public MoonPhasesImpl(final int year, final int month, final int day) {
        this.setDate(year, month, day);
    }

    /* ===================== API MoonPhases ================= */

    /**
     * Imposta la data target formattandola come richiesto da MoonGiant
     * (<code>dd/MM/yyyy</code>) e aggiorna <code>MOON_INFO</code> di base.
     */
    @Override
    public void setDate(final int year, final int month, final int day) {
        final String formatted = String.format("%02d/%02d/%d", day, month, year);
        this.date = formatted;
        this.MOON_INFO.put("date", formatted);
    }

    /**
     * Restituisce (eventualmente da cache) un <code>Map</code> contenente:
     * <ul>
     *   <li><code>date</code> – data richiesta</li>
     *   <li><code>state</code> – fase lunare es. "Gibbosa calante"</li>
     *   <li><code>illumination</code> – percentuale illuminazione es. "64%"</li>
     *   <li><code>image_name</code> – nome file PNG dell’immagine</li>
     * </ul>
     * @return {@link Optional#empty()} se il parsing fallisce.
     */
    @Override
    public Optional<Map<String, String>> getMoonInfo() {
        if (this.date.isEmpty()) {
            final LocalDate now = LocalDate.now();
            this.setDate(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        }
        if (this.CACHE.containsKey(this.date)) {
            return Optional.of(this.CACHE.get(this.date));
        }
        if (!this.retrieveMoonPhaseInfo()) {
            return Optional.empty();
        }
        // Salva una copia immutabile per evitare side-effect futuri
        this.CACHE.put(this.date, new HashMap<>(MOON_INFO));
        return Optional.of(this.MOON_INFO);
    }

    /** @return la data attualmente impostata (formato <code>dd/MM/yyyy</code>). */
    @Override
    public String getDate() { return this.date; }

    /**
     * Componi URL completo dell’immagine fase lunare.
     * @param image_name nome file PNG (es. "waxing_gibbous.png").
     */
    @Override
    public String getImageURL(final String image_name) {
        return BASE_IMG_URL + image_name;
    }

    /* ===================== metodi privati ================= */

    /**
     * Effettua lo scraping della pagina HTML e popola {@link #MOON_INFO}.
     * @return <code>true</code> se tutti i campi richiesti sono stati estratti.
     */
    private boolean retrieveMoonPhaseInfo() {
        try {
            final Document DOC = Jsoup.connect(SITE_URL + this.date).get();
            final Element all_info = DOC.getElementById("today_");
            if (all_info == null) return false;

            // Fase lunare (penultima riga di testo dentro il div)
            final String[] lines = all_info.html().trim().split("<br>");
            this.MOON_INFO.put("state", lines[lines.length - 2].trim());

            // Percentuale di illuminazione (primo <span>)
            final String percIllumination = all_info.getElementsByTag("span").first().text().trim();
            this.MOON_INFO.put("illumination", percIllumination);

            // Nome immagine (src dell'<img>)
            final Element img = all_info.getElementsByTag("img").first();
            final String[] imgNameSplitted = img.attr("src").split("/");
            this.MOON_INFO.put("image_name", imgNameSplitted[imgNameSplitted.length - 1]);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
