package org.app.weathermode.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * <h2>AdvancedJsonReader</h2>
 * <p>Interfaccia che definisce un <strong>wrapper avanzato</strong> per la
 * manipolazione di documenti JSON. Oltre alla semplice deserializzazione,
 * l’obiettivo è fornire un set di utility ad alto livello per:</p>
 * <ul>
 *   <li>scaricare JSON da URL remoti (<em>HTTP GET</em>);</li>
 *   <li>caricare stringhe/oggetti già presenti in memoria;</li>
 *   <li>navigare la struttura con un <em>path</em> a dot-notation
 *       (<code>a.b.c</code>);</li>
 *   <li>estrarre valori tipizzati evitando il casting esplicito;</li>
 *   <li>fare checking rapido dell’esistenza di un nodo.</li>
 * </ul>
 * I metodi che accedono al contenuto possono lanciare <code>Exception</code>
 * generiche per raggruppare errori di path e di tipo — l’implementazione è
 * responsabile di specificare le eccezioni concrete.
 */
public interface AdvancedJsonReader {

    /* ======================= caricamento ======================= */

    /**
     * Scarica il JSON da una URL effettuando una richiesta <em>HTTP GET</em>
     * sincrona; sostituisce eventuali dati precedenti presenti nell’istanza.
     *
     * @param jsonURL endpoint assoluto (http/https) del documento.
     * @throws IOException problemi di rete o se il server restituisce un errore.
     */
    void requestJSON(String jsonURL) throws IOException;

    /**
     * Imposta il JSON a partire da una stringa grezza.
     * @param jsonString payload completo (deve essere un JSON valido).
     * @return <code>this</code> per abilitare il <em>method-chaining</em>.
     */
    AdvancedJsonReaderImpl setJSON(String jsonString);

    /**
     * Variante che accetta direttamente un {@link JsonObject} già deserializzato.
     * @param jsonObj oggetto radice.
     */
    void setJSON(JsonObject jsonObj);

    /**
     * @return la stringa JSON originale così come caricata o scaricata.
     */
    String getRawJSON();

    /* ======================= navigazione ====================== */

    /**
     * Naviga il JSON seguendo un <em>path</em> con segmenti separati da punto
     * (es. <code>"hourly.temperature_2m"</code>) e restituisce l’oggetto
     * individuato.
     *
     * @param path dot-notation verso un nodo <strong>JsonObject</strong>.
     * @return l’oggetto trovato.
     * @throws Exception se uno dei segmenti non esiste o non è un oggetto.
     */
    JsonObject walkthroughBody(String path) throws Exception;

    /**
     * Recupera un {@link JsonArray} localizzato dal path.
     * @param path dot-notation che termina su un array.
     * @throws Exception path non valido o nodo non array.
     */
    JsonArray getJsonArray(String path) throws Exception;

    /**
     * Estrae il valore in <code>path</code> e lo converte nel tipo richiesto.
     * <p>Le implementazioni sono tenute a supportare almeno le classi wrapper
     * delle primitive, {@link String}, {@link JsonObject}, {@link JsonArray},
     * {@link JsonElement} e {@link Number}.</p>
     *
     * @param path dot-notation verso il valore finale.
     * @param type classe di destinazione.
     * @return valore convertito.
     * @throws Exception se il path è errato o la conversione impossibile.
     */
    <T> T getFromJson(String path, Class<T> type) throws Exception;

    /**
     * Versione non tipizzata che restituisce il {@link JsonElement} grezzo.
     */
    JsonElement getFromJson(String path) throws Exception;

    /**
     * Verifica la presenza di un nodo senza sollevare eccezioni.
     * @param path dot-notation.
     * @return <code>true</code> se esiste, altrimenti <code>false</code>.
     */
    boolean elementExists(String path);

    /* =================== helper tipizzati ===================== */

    String  getString(String path);
    Integer getInt(String path);
    Long    getLong(String path);
    Double  getDouble(String path);
    Float   getFloat(String path);
    boolean getBool(String path);

}
