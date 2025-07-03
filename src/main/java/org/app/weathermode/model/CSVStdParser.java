package org.app.weathermode.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
 * di parsare CSV provenienti da risorse di class‑path, stream di rete o altre
 * fonti senza dover prima materializzare un file su disco.</p>
 */
public class CSVStdParser extends CSVReader implements CSVParser {

    /* ========================= ctor ========================== */

    /**
     * Costruisce il parser associandolo al file CSV indicato.
     *
     * @param csvFilePath percorso del file.
     * @throws FileNotFoundException se il file non esiste o non è accessibile.
     */
    public CSVStdParser(final String csvFilePath) throws FileNotFoundException {
        super(new FileReader(csvFilePath));
    }

    /**
     * Costruisce il parser a partire da un {@link Reader}. Da usare quando il
     * CSV proviene da uno {@link InputStream} (ad esempio una risorsa
     * all’interno del <em>jar</em>) o qualsiasi altra fonte che possa fornire
     * un Reader.
     *
     * @param reader sorgente dei dati CSV già incapsulata in un Reader.
     */
    public CSVStdParser(final Reader reader) {
        super(reader);
    }

    /* ======================= API CSVParser ==================== */

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

    @Override
    public List<String> getHeader() throws IOException, CsvException {
        final List<String[]> allRows = this.readAll();
        return allRows.isEmpty() ? Collections.emptyList() : Arrays.asList(allRows.get(0));
    }

}
