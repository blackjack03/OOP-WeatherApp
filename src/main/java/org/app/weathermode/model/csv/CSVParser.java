package org.app.weathermode.model.csv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;

/**
 * <h2>CSVParser</h2>
 * <p>Interfaccia <em>SPI</em> (Service Provider Interface) astratta che
 * definisce le operazioni minime per trasformare un file <strong>CSV</strong>
 * in strutture dati native Java.</p>
 */
public interface CSVParser {

    /**
     * Legge l’intero contenuto del CSV e converte ogni riga in una
     * {@link Map} in cui le chiavi sono i nomi di colonna (prima riga del file)
     * e i valori le celle corrispondenti.
     *
     * @return lista di mappe, <em>vuota</em> se il CSV non contiene righe.
     * @throws IOException  se si verifica un errore di I/O durante la lettura
     *                      dal filesystem o da uno <code>InputStream</code>.
     * @throws CsvException se il file non rispetta la sintassi CSV (es. campi
     *                      non chiusi, numero di colonne incoerente).
     */
    List<Map<String, String>> readCSVToMap() throws IOException, CsvException;

    /**
     * Restituisce l’intestazione (header) del CSV, ovvero la lista dei nomi di
     * colonna presenti nella prima riga del file.
     *
     * @return lista di stringhe, <em>vuota</em> se il file è privo di righe.
     * @throws IOException  problemi di I/O come per {@link #readCSVToMap()}.
     * @throws CsvException problemi di formattazione del CSV.
     */
    List<String> getHeader() throws IOException, CsvException;

}
