package org.app.weathermode.model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <h2>LocationSelectorImpl</h2>
 * <p>Implementazione di {@link LocationSelector} basata sul CSV
 * <em>worldcities.csv</em> reso disponibile come risorsa di class‑path.
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

    /** Percorso della risorsa CSV nel class‑path. */
    private static final String CSV_RESOURCE = "/files/worldcities.csv";

    /** Copia completa del CSV in memoria. */
    private final List<Map<String, String>> CSV;

    /** Indice <code>ID → riga</code> per accesso <em>O(1)</em>. */
    private final Map<Integer, Map<String, String>> CITIES_MAP = new HashMap<>();

    /**
     * Carica il CSV da class‑path e costruisce l’indice delle città.
     * Solleva {@link Error} in caso di problemi non recuperabili (risorsa
     * assente o CSV malformato).
     */
    public LocationSelectorImpl() {
        try (InputStream is = getClass().getResourceAsStream(CSV_RESOURCE)) {
            if (is == null) {
                throw new FileNotFoundException("Resource " + CSV_RESOURCE + " not found in classpath");
            }

            try (CSVStdParser parser = new CSVStdParser(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                this.CSV = parser.readCSVToMap();
            }

            for (Map<String, String> row : CSV) {
                int idx = Integer.parseInt(row.get("id"));
                this.CITIES_MAP.put(idx, row);
            }
        } catch (Exception err) {
            throw new Error("Unable to load city list", err);
        }
    }

    /* ====================== API LocationSelector ===================== */

    @Override
    public List<Pair<String, Integer>> getPossibleLocations(final String txt) {
        List<Pair<String, Integer>> possibleLocations = new ArrayList<>();
        String query = txt.toLowerCase(Locale.ROOT);

        for (Map<String, String> entry : this.CSV) {
            String cityName = entry.get("city").toLowerCase(Locale.ROOT);
            String cityNameAscii = entry.get("city_ascii").toLowerCase(Locale.ROOT);
            if (cityName.contains(query) || cityNameAscii.contains(query)) {
                String completeName = String.format("%s, %s, %s",
                        entry.get("city"), entry.get("admin_name"), entry.get("country"));
                possibleLocations.add(new Pair<>(completeName, Integer.parseInt(entry.get("id"))));
            }
        }
        return possibleLocations;
    }

    @Override
    public Optional<Map<String, String>> getByID(final int ID) {
        return Optional.ofNullable(this.CITIES_MAP.get(ID));
    }

    @Override
    public Optional<Integer> searchByLookUp(final LookUp lookUp) {
        if (lookUp == null) return Optional.empty();
        String city = lookUp.getCity();
        String countryCode = lookUp.getCountryCode();
        for (var entry : this.CITIES_MAP.entrySet()) {
            Map<String, String> cityData = entry.getValue();
            if (cityData.get("city").equalsIgnoreCase(city) &&
                cityData.get("iso2").equalsIgnoreCase(countryCode)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
