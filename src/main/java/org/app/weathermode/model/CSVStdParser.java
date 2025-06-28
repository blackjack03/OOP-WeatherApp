package org.app.weathermode.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
 * <p>Il costruttore apre in sola lettura il percorso specificato e delega la
 * gestione a {@link CSVReader}. Eventuali eccezioni di I/O vengono propagate
 * affinché il chiamante possa gestirle.</p>
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

    /* ======================= API CSVParser ==================== */

    /**
     * Legge l’intero file e crea per ogni riga una {@link Map} in cui le chiavi
     * sono i nomi di colonna (prima riga del file) e i valori le celle
     * corrispondenti.
     *
     * @return lista di mappe, vuota se il CSV non contiene righe.
     * @throws IOException  problemi di lettura dal filesystem.
     * @throws CsvException problemi di parsing (malformed CSV, etc.).
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
     * @return la lista dei nomi di colonna presenti nella prima riga del file;
     *         se il file è vuoto restituisce una lista vuota.
     */
    @Override
    public List<String> getHeader() throws IOException, CsvException {
        final List<String[]> allRows = this.readAll();
        return allRows.isEmpty() ? Collections.emptyList() : Arrays.asList(allRows.get(0));
    }

}
