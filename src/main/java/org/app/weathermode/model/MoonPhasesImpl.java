package org.app.weathermode.model;

// CHECKSTYLE: AvoidStarImport OFF
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.util.*;
// CHECKSTYLE: AvoidStarImport ON

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
    private final Map<String, String> moonInfo = new HashMap<>();
    /** Cache per evitare ripetuti scraping della stessa data. */
    private final Map<String, Map<String, String>> cache = new HashMap<>();
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
    public final void setDate(final int year, final int month, final int day) {
        final String formatted = String.format("%02d/%02d/%d", day, month, year);
        this.date = formatted;
        this.moonInfo.put("date", formatted);
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
        if (this.cache.containsKey(this.date)) {
            return Optional.of(this.cache.get(this.date));
        }
        if (!this.retrieveMoonPhaseInfo()) {
            return Optional.empty();
        }
        // Salva una copia immutabile per evitare side-effect futuri
        this.cache.put(this.date, new HashMap<>(moonInfo));
        return Optional.of(this.moonInfo);
    }

    /** @return la data attualmente impostata (formato <code>dd/MM/yyyy</code>). */
    @Override
    public String getDate() {
        return this.date;
    }

    /**
     * Componi URL completo dell’immagine fase lunare.
     * @param imageName nome file PNG (es. "waxing_gibbous.png").
     */
    @Override
    public String getImageURL(final String imageName) {
        return BASE_IMG_URL + imageName;
    }

    /* ===================== metodi privati ================= */

    /**
     * Effettua lo scraping della pagina HTML e popola {@link #moonInfo}.
     * @return <code>true</code> se tutti i campi richiesti sono stati estratti.
     */
    private boolean retrieveMoonPhaseInfo() {
        try {
            final Document doc = Jsoup.connect(SITE_URL + this.date).get();
            final Element allInfo = doc.getElementById("today_");
            if (allInfo == null) {
                return false;
            }

            // Fase lunare (penultima riga di testo dentro il div)
            final String[] lines = allInfo.html().trim().split("<br>");
            this.moonInfo.put("state", lines[lines.length - 2].trim());

            // Percentuale di illuminazione (primo <span>)
            final String percIllumination = allInfo.getElementsByTag("span").first().text().trim();
            this.moonInfo.put("illumination", percIllumination);

            // Nome immagine (src dell'<img>)
            final Element img = allInfo.getElementsByTag("img").first();
            final String[] imgNameSplitted = img.attr("src").split("/");
            this.moonInfo.put("image_name", imgNameSplitted[imgNameSplitted.length - 1]);
            return true;
        } catch (final Exception e) { // NOPMD
            return false;
        }
    }

}
