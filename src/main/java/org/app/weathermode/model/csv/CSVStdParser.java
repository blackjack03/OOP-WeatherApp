package org.app.weathermode.model.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

/**
 * <h2>CSVStdParser</h2>
 * <p>Adattatore che estende {@link CSVReader} di <em>OpenCSV</em> e implementa
 * l’interfaccia applicativa {@link CSVParser}. La classe fornisce due comode
 * utility:</p>
 * <ul>
 *   <li>{@link #readCSVToMap()} – converte un file CSV in una lista di mappe
 *       <code>header&rarr;valore</code>, una per riga.</li>
 *   <li>{@link #getHeader()} – restituisce l’intestazione del file come lista
 *       di stringhe.</li>
 * </ul>
 * <p>Oltre al costruttore classico che accetta un percorso di file, è stato
 * aggiunto un costruttore che riceve qualunque {@link Reader}. Questo permette
 * di parsare CSV provenienti da risorse di class-path, stream di rete o altre
 * fonti senza dover prima materializzare un file su disco.</p>
 */
public class CSVStdParser extends CSVReader implements CSVParser {

    /* ========================= ctor ========================== */

    /**
     * Costruisce un parser leggendo il CSV dal file specificato.
     *
     * @param csvFilePath percorso del file CSV da aprire
     * @throws IOException in caso di errori di I/O durante la lettura
     */
    public CSVStdParser(final String csvFilePath) throws IOException {
        super(Files.newBufferedReader(Paths.get(csvFilePath), StandardCharsets.UTF_8));
    }

    /**
     * Costruisce un parser utilizzando il {@link Reader} fornito
     * come sorgente dei dati CSV.
     *
     * @param reader sorgente dei dati CSV già incapsulata in un {@link Reader}
     */
    public CSVStdParser(final Reader reader) {
        super(reader);
    }

    /* ======================= API CSVParser ==================== */

    /**
     * Legge l’intero contenuto del CSV e lo converte in una lista di mappe,
     * dove ogni mappa rappresenta una riga con chiavi prese dall’intestazione.
     *
     * @return lista di mappe <code>header → valore</code>, una per riga del CSV
     * @throws IOException  in caso di errori di I/O durante la lettura
     * @throws CsvException in caso di errore di parsing CSV
     */
    @Override
    public List<Map<String, String>> readCSVToMap() throws IOException, CsvException {
        final List<Map<String, String>> resultList = new ArrayList<>();
        final List<String[]> allRows = this.readAll();

        if (allRows.isEmpty()) {
            return resultList;
        }

        final String[] header = allRows.get(0);

        for (int i = 1; i < allRows.size(); i++) {
            final String[] row = allRows.get(i);
            final Map<String, String> rowMap = new HashMap<>();

            for (int j = 0; j < header.length; j++) {
                rowMap.put(header[j], j < row.length ? row[j] : "");
            }
            resultList.add(rowMap);
        }
        return resultList;
    }

    /**
     * Restituisce l’intestazione del CSV come lista di stringhe,
     * corrispondenti ai nomi delle colonne.
     *
     * @return lista di intestazioni (colonne) del CSV; vuota se il file non ha righe
     * @throws IOException  in caso di errori di I/O durante la lettura
     * @throws CsvException in caso di errore di parsing CSV
     */
    @Override
    public List<String> getHeader() throws IOException, CsvException {
        final List<String[]> allRows = this.readAll();
        return allRows.isEmpty()
            ? Collections.emptyList()
            : Arrays.asList(allRows.get(0));
    }

}
