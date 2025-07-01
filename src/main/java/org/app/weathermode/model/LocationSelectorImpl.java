package org.app.weathermode.model;

import java.util.*;
import java.io.*;

/**
 * <h2>LocationSelectorImpl</h2>
 * <p>Implementazione di {@link LocationSelector} basata su un CSV contenente
 * oltre 40.000 toponimi (worldcities.csv). La classe offre tre principali
 * funzionalità:</p>
 * <ul>
 *   <li><strong>lookup testuale</strong> di possibili località tramite
 *       {@link #getPossibleLocations(String)};</li>
 *   <li>accesso diretto alla riga del CSV via ID univoco
 *       ({@link #getByID(int)});</li>
 *   <li>ricerca automatica di una città a partire dai dati di
 *       {@link LookUp} (tipicamente l’IP geolookup) con
 *       {@link #searchByLookUp(LookUp)}.</li>
 * </ul>
 * <p>Al primo avvio il costruttore legge l’intero CSV in memoria e crea una
 * <code>HashMap&lt;ID,Record&gt;</code> per rendere le ricerche per ID
 * <em>O(1)</em>. Il parsing è delegato a {@link CSVStdParser}.</p>
 */
public class LocationSelectorImpl implements LocationSelector {

    /**
     * Percorso al CSV worldcities. Viene costruito in maniera portabile
     * concatenando {@link File#separator} per evitare problemi tra Windows/Linux.
     */
    private static final String CSV_PATH =
            "src%Smain%Sjava%Sorg%Sfiles%Sworldcities.csv".replace("%S", File.separator);

    /** Copia completa del CSV come lista di mappe (una per riga). */
    private final List<Map<String, String>> CSV;
    /** Indice <code>ID&rarr;riga</code> per accesso rapido. */
    private final Map<Integer, Map<String, String>> CITIES_MAP = new HashMap<>();

    /**
     * Legge il CSV e costruisce l’indice delle città. In caso di qualunque
     * eccezione fatale (file mancante o CSV malformato) viene sollevato un
     * {@link Error} in modo da interrompere il boot: senza la lista città
     * l’applicazione non può funzionare correttamente.
     */
    public LocationSelectorImpl() {
        try {
            final CSVStdParser parser = new CSVStdParser(CSV_PATH);
            this.CSV = parser.readCSVToMap();
            for (Map<String, String> row : CSV) {
                final int idx = Integer.parseInt(row.get("id"));
                this.CITIES_MAP.put(idx, row);
            }
            parser.close();
        } catch (final Exception err) {
            throw new Error("Unable to load city list", err);
        }
        System.out.println("Cities ready!");
    }

    /* ====================== API LocationSelector =============== */

    /**
     * Effettua una ricerca case‑insensitive sulle colonne <code>city</code> e
     * <code>city_ascii</code> restituendo le località che contengono la query
     * come sotto‑stringa.
     *
     * @param txt stringa da cercare.
     * @return lista di coppie <em>nome completo</em> ↔ <em>ID</em>, ordinata
     *         come appaiono nel CSV.
     */
    @Override
    public List<Pair<String, Integer>> getPossibleLocations(final String txt) {
        final List<Pair<String, Integer>> possibleLocations = new ArrayList<>();
        final String query = txt.toLowerCase(Locale.ROOT);

        for (final Map<String, String> entry : this.CSV) {
            final String cityName = entry.get("city").toLowerCase(Locale.ROOT);
            final String cityNameAscii = entry.get("city_ascii").toLowerCase(Locale.ROOT);
            if (cityName.contains(query) || cityNameAscii.contains(query)) {
                final String completeName = String.format("%s, %s, %s",
                        entry.get("city"), entry.get("admin_name"), entry.get("country"));
                possibleLocations.add(new Pair<>(completeName, Integer.parseInt(entry.get("id"))));
            }
        }
        return possibleLocations;
    }

    /**
     * Recupera la riga del CSV a partire dal suo ID.
     *
     * @param ID identificativo univoco della città.
     * @return {@link Optional} con la <code>Map&lt;colonna,valore&gt;</code> o
     *         vuoto se l’ID non esiste.
     */
    @Override
    public Optional<Map<String, String>> getByID(final int ID) {
        return Optional.ofNullable(this.CITIES_MAP.get(ID));
    }

    /**
     * Tenta di individuare l’ID città corrispondente a un {@link LookUp}
     * geografico (tipicamente ottenuto da IP). Confronta nome città e codice
     * ISO‑2 del paese.
     *
     * @param lookUp implementazione di {@link LookUp} o <code>null</code>.
     * @return ID della città corrispondente o {@link Optional#empty()} se non
     *         trovata.
     */
    @Override
    public Optional<Integer> searchByLookUp(final LookUp lookUp) {
        if (lookUp == null) return Optional.empty();
        final String city = lookUp.getCity();
        final String country_code = lookUp.getCountryCode();
        for (final var entry : this.CITIES_MAP.entrySet()) {
            final Map<String, String> cityData = entry.getValue();
            if (cityData.get("city").equalsIgnoreCase(city) &&
                cityData.get("iso2").equalsIgnoreCase(country_code)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

}
