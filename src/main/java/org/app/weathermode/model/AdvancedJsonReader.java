package org.app.weathermode.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
 * <p>I metodi che accedono al contenuto possono lanciare
 * <code>Exception</code> generiche per raggruppare errori di path e di tipo —
 * l’implementazione è responsabile di specificare le eccezioni concrete.</p>
 */
@SuppressFBWarnings(
    value = "THROWS_METHOD_THROWS_CLAUSE_BASIC_EXCEPTION",
    justification = "Throwing a generic exception here to signal a critical failure,"
    + "which is caught and handled by the global exception handler"
)
public interface AdvancedJsonReader {

    /* ======================= caricamento ======================= */

    /**
     * Scarica il JSON da una URL effettuando una richiesta <em>HTTP GET</em>
     * sincrona; sostituisce eventuali dati precedenti presenti nell’istanza.
     *
     * @param jsonURL endpoint assoluto (http/https) del documento JSON.
     * @throws IOException in caso di problemi di rete o se il server restituisce un errore.
     */
    void requestJSON(String jsonURL) throws IOException; // NOPMD

    /**
     * Imposta il JSON a partire da una stringa grezza.
     *
     * @param jsonString payload completo (deve essere un JSON valido).
     * @return questa istanza per abilitare il <em>method-chaining</em>.
     */
    AdvancedJsonReader setJSON(String jsonString); // NOPMD

    /**
     * Variante che accetta direttamente un {@link JsonObject} già deserializzato.
     *
     * @param jsonObj oggetto radice JSON.
     */
    void setJSON(JsonObject jsonObj);

    /**
     * Restituisce la stringa JSON originale così come caricata o scaricata.
     *
     * @return testo JSON non formattato o grezzo.
     */
    String getRawJSON();

    /* ======================= navigazione ====================== */

    /**
     * Naviga il JSON seguendo un <em>path</em> con segmenti separati da punto
     * (es. <code>"hourly.temperature_2m"</code>) e restituisce l’oggetto
     * individuato.
     *
     * @param path dot-notation verso un nodo <strong>JsonObject</strong>.
     * @return {@link JsonObject} individuato.
     * @throws Exception se uno dei segmenti non esiste o non rappresenta un oggetto JSON.
     */
    JsonObject walkthroughBody(String path) throws Exception; // NOPMD

    /**
     * Recupera un {@link JsonArray} localizzato dal path specificato.
     *
     * @param path dot-notation che termina su un array JSON.
     * @return {@link JsonArray} corrispondente al percorso.
     * @throws Exception se il path non è valido o il nodo non è un array.
     */
    JsonArray getJsonArray(String path) throws Exception; // NOPMD

    /**
     * Estrae il valore in <code>path</code> e lo converte nel tipo richiesto.
     * <p>Le implementazioni sono tenute a supportare almeno le classi wrapper
     * delle primitive, {@link String}, {@link JsonObject}, {@link JsonArray},
     * {@link JsonElement} e {@link Number}.</p>
     *
     * @param <T> tipo di ritorno desiderato.
     * @param path dot-notation verso il valore finale.
     * @param type classe di destinazione del valore.
     * @return valore convertito nel tipo specificato.
     * @throws Exception se il path è errato o la conversione non è possibile.
     */
    <T> T getFromJson(String path, Class<T> type) throws Exception; // NOPMD

    /**
     * Versione non tipizzata che restituisce il {@link JsonElement} grezzo
     * individuato dal path.
     *
     * @param path dot-notation verso l’elemento.
     * @return {@link JsonElement} corrispondente al percorso.
     * @throws Exception se il path non è valido o l’elemento non esiste.
     */
    JsonElement getFromJson(String path) throws Exception; // NOPMD

    /**
     * Verifica la presenza di un nodo senza sollevare eccezioni.
     *
     * @param path dot-notation verso il nodo da controllare.
     * @return <code>true</code> se il nodo esiste, <code>false</code> altrimenti.
     */
    boolean elementExists(String path);

    /* =================== helper tipizzati ===================== */

    /**
     * Shortcut per ottenere un valore {@link String} dal JSON.
     *
     * @param path dot-notation verso un elemento stringa.
     * @return valore stringa.
     */
    String getString(String path);

    /**
     * Shortcut per ottenere un valore {@link Integer} dal JSON.
     *
     * @param path dot-notation verso un elemento numerico intero.
     * @return valore intero.
     */
    Integer getInt(String path);

    /**
     * Shortcut per ottenere un valore {@link Long} dal JSON.
     *
     * @param path dot-notation verso un elemento numerico long.
     * @return valore long.
     */
    Long getLong(String path);

    /**
     * Shortcut per ottenere un valore {@link Double} dal JSON.
     *
     * @param path dot-notation verso un elemento numerico double.
     * @return valore double.
     */
    Double getDouble(String path);

    /**
     * Shortcut per ottenere un valore {@link Float} dal JSON.
     *
     * @param path dot-notation verso un elemento numerico float.
     * @return valore float.
     */
    Float getFloat(String path);

    /**
     * Shortcut per ottenere un valore booleano dal JSON.
     *
     * @param path dot-notation verso un elemento booleano.
     * @return valore booleano.
     */
    boolean getBool(String path);

}
