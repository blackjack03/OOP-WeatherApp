package org.app.weathermode.model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// CHECKSTYLE: AvoidStarImport OFF
import java.util.*;

// CHECKSTYLE: AvoidStarImport ON

/**
 * <h2>LocationSelectorImpl</h2>
 * <p>Implementazione di {@link LocationSelector} basata sul CSV
 * <em>worldcities.csv</em> reso disponibile come risorsa di class-path.
 * La classe offre tre funzionalità principali:</p>
 * <ul>
 *   <li><strong>lookup testuale</strong> tramite {@link #getPossibleLocations(String)};</li>
 *   <li>accesso diretto alla riga del CSV via ID con {@link #getByID(int)};</li>
 *   <li>ricerca automatica della città da dati di {@link LookUp} con
 *       {@link #searchByLookUp(LookUp)}.</li>
 * </ul>
 * <p>All’avvio l’intero CSV viene caricato in memoria e indicizzato in una
 * <code>HashMap&lt;ID,Record&gt;</code> per ricerche <em>O(1)</em>. Il parsing è
 * delegato a {@link CSVStdParser}.</p>
 */
public class LocationSelectorImpl implements LocationSelector {

    /** Percorso della risorsa CSV nel class-path. */
    private static final String CSV_RESOURCE = "/files/worldcities.csv";

    /** Copia completa del CSV in memoria. */
    private final List<Map<String, String>> csv;

    /** Indice <code>ID → riga</code> per accesso <em>O(1)</em>. */
    private final Map<Integer, Map<String, String>> citiesMap = new HashMap<>();

    /**
     * Carica il CSV da class-path e costruisce l’indice delle città.
     * Solleva {@link Error} in caso di problemi non recuperabili (risorsa
     * assente o CSV malformato).
     */
    public LocationSelectorImpl() {
        try (InputStream is = LocationSelector.class.getResourceAsStream(CSV_RESOURCE)) {
            if (is == null) {
                throw new FileNotFoundException("Resource " + CSV_RESOURCE + " not found in classpath");
            }

            try (CSVStdParser parser = new CSVStdParser(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                this.csv = parser.readCSVToMap();
            }

            for (final Map<String, String> row : csv) {
                final int idx = Integer.parseInt(row.get("id"));
                this.citiesMap.put(idx, row);
            }
        } catch (final Exception err) { // NOPMD
            throw new Error("Unable to load city list", err); // NOPMD
        }
    }

    /* ====================== API LocationSelector ===================== */

    /**
     * Restituisce l’elenco delle possibili corrispondenze per una stringa di
     * ricerca parziale sui nomi di città (case-insensitive), includendo
     * città ASCII e locale.
     *
     * @param txt la stringa di ricerca (o parte di essa)
     * @return una {@link List} di {@link Pair} in cui:
     *         <ul>
     *           <li>la prima componente è la descrizione completa
     *               "<em>città, regione, paese</em>"</li>
     *           <li>la seconda è l’ID numerico corrispondente alla riga CSV</li>
     *         </ul>
     */
    @Override
    public List<Pair<String, Integer>> getPossibleLocations(final String txt) {
        final List<Pair<String, Integer>> possibleLocations = new ArrayList<>();
        final String query = txt.toLowerCase(Locale.ROOT);

        for (final Map<String, String> entry : this.csv) {
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
     * Recupera direttamente la mappa di valori di una città dato il suo ID.
     *
     * @param id l’identificativo numerico della città (colonna "id" nel CSV)
     * @return un {@link Optional} contenente la riga CSV come
     *         {@code Map<String,String>} se esiste, altrimenti vuoto
     */
    @Override
    public Optional<Map<String, String>> getByID(final int id) {
        return Optional.ofNullable(this.citiesMap.get(id));
    }

    /**
     * Cerca automaticamente l’ID di una città basandosi su un oggetto {@link LookUp}
     * che contiene nome città e codice ISO del paese.
     *
     * @param lookUp l’oggetto {@link LookUp} con i campi {@code city}
     *               e {@code countryCode} (ISO2)
     * @return un {@link Optional} contenente l’ID numerico se trovato,
     *         altrimenti vuoto
     */
    @Override
    public Optional<Integer> searchByLookUp(final LookUp lookUp) {
        if (lookUp == null) {
            return Optional.empty();
        }
        final String city = lookUp.getCity();
        final String countryCode = lookUp.getCountryCode();
        for (final var entry : this.citiesMap.entrySet()) {
            final Map<String, String> cityData = entry.getValue();
            if (cityData.get("city").equalsIgnoreCase(city)
                && cityData.get("iso2").equalsIgnoreCase(countryCode)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

}
