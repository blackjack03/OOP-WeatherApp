package org.app.weathermode.model.locationselector;

// CHECKSTYLE: AvoidStarImport OFF
import org.app.weathermode.model.lookup.LookUp;
import org.app.weathermode.model.pair.Pair;

import java.util.*;
// CHECKSTYLE: AvoidStarImport ON

/**
 * <h2>LocationSelector</h2>
 * <p>Interfaccia che incapsula la logica di <strong>ricerca e selezione
 * località</strong> all’interno dell’applicazione. Le implementazioni si
 * appoggiano CSV di città, ma la sorgente è
 * astratta per permettere soluzioni alternative (e.g. API).
 */
public interface LocationSelector {

    /**
     * Effettua una ricerca <em>case-insensitive</em> restituendo le località
     * che contengono la stringa <code>txt</code> nel nome (o sua variante ASCII).
     *
     * @param txt stringa immessa dall’utente (può essere parziale).
     * @return lista di coppie { "Nome completo città, Regione, Paese", ID }.
     */
    List<Pair<String, Integer>> getPossibleLocations(String txt);

    /**
     * Recupera i dettagli della località a partire dal suo identificativo.
     *
     * @param id chiave primaria del record città.
     * @return mappa <code>campo→valore</code> o {@link Optional#empty()} se non trovato.
     */
    Optional<Map<String, String>> getByID(int id);

    /**
     * Tenta di mappare i dati derivanti da un {@link LookUp} (es. IP
     * geolocalizzato) a un ID città presente nel database interno.
     * L’implementazione decide la strategia di matching (nome, nazione, lat/lon…).
     *
     * @param lookUp istanza già popolata; può essere <code>null</code>.
     * @return ID città se associata, altrimenti {@link Optional#empty()}.
     */
    Optional<Integer> searchByLookUp(LookUp lookUp);

}
