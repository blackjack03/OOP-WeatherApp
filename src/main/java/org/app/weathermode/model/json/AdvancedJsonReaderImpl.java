package org.app.weathermode.model.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// CHECKSTYLE: AvoidStarImport OFF
import com.google.gson.*;
// CHECKSTYLE: AvoidStarImport ON

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>Implementazione concreta di {@link AdvancedJsonReader} in grado di:
 * <ul>
 *   <li>scaricare un documento JSON da una URL remota (HTTP GET);</li>
 *   <li>accettare una stringa o un {@link JsonObject} già disponibile in memoria;</li>
 *   <li>navigare il JSON attraverso un <em>path</em> stile <code>a.b.c</code>
 *       restituendo oggetti, array o primiti tipizzate;</li>
 *   <li>eseguire conversioni di tipo <em>safe</em> gestendo errori di
 *       formattazione o di accesso a chiavi inesistenti.</li>
 * </ul>
 * Prima di invocare qualsiasi metodo di lettura è necessario inizializzare
 * l'istanza con una delle varianti di <em>setter</em> (<code>requestJSON</code>,
 * <code>setJSON</code> ecc.). Una volta impostato, l'oggetto diventa <em>read‑
 * only</em> e non può essere re‑inizializzato (vedi {@link #assertNotAlreadySet()}).
 * </p>
 */
public class AdvancedJsonReaderImpl implements AdvancedJsonReader {

    /* ===================== stato interno ===================== */
    private static final String DEFAULT_SEPARATOR = "\\.";
    /** Rappresentazione testuale (non formattata) del JSON sorgente. */
    private String jsonRawText;
    /** Radice dell'albero JSON in forma di {@link JsonObject}. */
    private JsonObject jsonBody;
    /** Flag che indica se il JSON è già stato impostato. */
    private boolean isSet;

    /* ========================= ctor ========================== */
    /** Costruttore vuoto; il JSON verrà impostato successivamente. */
    public AdvancedJsonReaderImpl() { /* empty body */ }

    /**
     * Costruttore che scarica immediatamente il JSON da una URL.
     *
     * @param jsonURL URL da cui effettuare la richiesta HTTP GET.
     * @throws IOException in caso di problemi di rete o risposta non valida.
     */
    public AdvancedJsonReaderImpl(final String jsonURL) throws IOException {
        requestJSON(jsonURL);
    }

    /**
     * Costruttore che inizializza direttamente con un {@link JsonObject} già
     * disponibile.
     *
     * @param jsonObj oggetto JSON radice.
     */
    public AdvancedJsonReaderImpl(final JsonObject jsonObj) {
        this.jsonRawText = jsonObj.toString();
        parseAndSetJson();
    }

    /* ==================== caricamento JSON =================== */

    /**
     * Scarica il JSON via HTTP, lo memorizza come stringa e ne effettua il
     * parsing in {@link #jsonBody}.
     * <p>Può essere invocato una sola volta per istanza – in caso contrario
     * viene sollevata {@link IllegalStateException}.</p>
     *
     * @param jsonURL URL della risorsa JSON.
     * @throws IOException problemi di I/O o rete.
     */
    @Override
    public final void requestJSON(final String jsonURL)
        throws IOException, IllegalStateException { // NOPMD
        assertNotAlreadySet();

        final URL url = new URL(jsonURL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream in = connection.getInputStream();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            final StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) { // NOPMD
                jsonText.append(line);
            }
            this.jsonRawText = jsonText.toString();
        } finally {
            connection.disconnect();
        }

        parseAndSetJson();
    }

    /**
     * Imposta il JSON a partire da una stringa già disponibile.
     *
     * @param jsonString testo JSON.
     * @return <code>this</code> per <em>method chaining</em>.
     * @throws IllegalStateException se l'oggetto era già stato inizializzato.
     */
    @Override
    public AdvancedJsonReaderImpl setJSON(final String jsonString) { // NOPMD
        assertNotAlreadySet();
        this.jsonRawText = jsonString;
        parseAndSetJson();
        return this;
    }

    /**
     * Versione di {@link #setJSON(String)} che accetta direttamente un
     * {@link JsonObject} radice.
     *
     * @param jsonObj radice JSON.
     * @throws IllegalStateException se l'oggetto era già stato inizializzato.
     */
    @Override
    public void setJSON(final JsonObject jsonObj) {
        assertNotAlreadySet();
        this.jsonRawText = jsonObj.toString();
        parseAndSetJson();
    }

    /* ========================== getter ========================= */

    /**
     * @return la stringa JSON originale (così com'è stata scaricata o passata).
     * @throws UnsupportedOperationException se il JSON non è stato ancora
     *                                       impostato.
     */
    @Override
    public String getRawJSON() {
        assertIsSet();
        return this.jsonRawText;
    }

    /* =================== navigazione struttura ================== */

    /**
     * Naviga l'albero JSON seguendo un <em>path</em> puntato a un oggetto e ne
     * restituisce la {@link JsonObject} corrispondente.
     *
     * @param path stringa con livelli separati da ".", es. "coord.latlng".
     * @return l'oggetto individuato.
     * @throws IllegalArgumentException se uno dei nodi del path non esiste.
     * @throws Exception                propagata da {@link #walkthroughBody} annidata
     *                                  in metodi che la richiedono.
     */
    @Override
    public JsonObject walkthroughBody(final String path) throws Exception {
        this.assertIsSet();
        final String[] parts = path.split(DEFAULT_SEPARATOR);
        if (parts.length == 0) {
            return null;
        }
        JsonObject output = this.jsonBody.getAsJsonObject(parts[0]);
        if (output == null) {
            throw new IllegalArgumentException("\"" + parts[0] + "\" not found!");
        }
        for (int i = 1; i < parts.length; i++) {
            output = output.getAsJsonObject(parts[i]);
            if (output == null) {
                throw new IllegalArgumentException("In path: \"" + parts[i] + "\" no member with this name exists!");
            }
        }
        return output;
    }

    /**
     * Estrae un {@link JsonArray} localizzato dal <em>path</em> indicato.
     *
     * @param path livelli separati da punto, con l'ultimo che dev'essere un array.
     * @return l'array JSON corrispondente.
     * @throws Exception se il path è errato o non punta ad un array.
     */
    @Override
    public JsonArray getJsonArray(final String path) throws Exception {
        assertIsSet();
        final String[] parts = path.split(DEFAULT_SEPARATOR);

        final String newPath = getLevelUpPath(parts);

        // CHECKSTYLE: FinalLocalVariable OFF
        // False Positive
        JsonArray jsonArray; // NOPMD suppressed as it is a false positive
        if (parts.length > 1) {
            jsonArray = this.walkthroughBody(newPath).get(parts[parts.length - 1]).getAsJsonArray();
        } else {
            jsonArray = this.jsonBody.get(parts[0]).getAsJsonArray();
        }
        // CHECKSTYLE: FinalLocalVariable ON

        return jsonArray;
    }

    /**
     * Recupera e converte l'elemento individuato dal <em>path</em> nel tipo
     * specificato.
     * <p>Supporta le classi wrapper di primitive, {@link String},
     * {@link JsonObject}, {@link JsonArray}, {@link JsonPrimitive} e
     * {@link Number} generico.</p>
     *
     * @param path path verso l'elemento.
     * @param type classe di destinazione.
     * @param <T>  tipo di ritorno.
     * @return elemento convertito.
     * @throws IllegalArgumentException se il tipo non è supportato o l'elemento
     *                                  non esiste.
     * @throws Exception propagata da metodi interni.
     */
    @Override
    public <T> T getFromJson(final String path, final Class<T> type) throws Exception {
        assertIsSet();
        final String[] parts = path.split(DEFAULT_SEPARATOR);
        if (parts.length == 0 || path.trim().isEmpty()) { // NOPMD
            return null;
        }
        final String elemToGet = parts[parts.length - 1];
        final String newPath = getLevelUpPath(parts);
        // CHECKSTYLE: FinalLocalVariable OFF
        // False Positive
        JsonElement element; // NOPMD suppressed as it is a false positive
        if (parts.length > 1) {
            final JsonObject finalLevel = this.walkthroughBody(newPath);
            element = finalLevel.get(elemToGet);
        } else {
            element = this.jsonBody.get(elemToGet);
        }
        // CHECKSTYLE: FinalLocalVariable ON

        if (element == null) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists!");
        }

        // CHECKSTYLE: FinalLocalVariable OFF
        T outElem; // NOPMD suppressed as it is a false positive
        if (type.equals(String.class)) {
            outElem = type.cast(element.getAsString());
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            outElem = type.cast(element.getAsBoolean());
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            outElem = type.cast(element.getAsDouble());
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            outElem = type.cast(element.getAsFloat());
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            outElem = type.cast(element.getAsInt());
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            outElem = type.cast(element.getAsLong());
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            outElem = type.cast(element.getAsShort());
        } else if (type.equals(JsonArray.class)
                || type.equals(JsonObject.class)
                || type.equals(JsonPrimitive.class)) {
            outElem = type.cast(element);
        } else if (type.equals(Number.class)) {
            outElem = type.cast(element.getAsNumber());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        return outElem;
        // CHECKSTYLE: FinalLocalVariable ON
    }

    /**
     * Versione non tipizzata di {@link #getFromJson(String, Class)} che
     * restituisce direttamente il {@link JsonElement} grezzo.
     */
    @Override
    public JsonElement getFromJson(final String path) throws Exception {
        assertIsSet();
        final String[] parts = path.split(DEFAULT_SEPARATOR);
        final String elemToGet = parts[parts.length - 1];

        final String newPath = getLevelUpPath(parts);

        // CHECKSTYLE: FinalLocalVariable OFF
        // False Positive
        JsonElement element; // NOPMD suppressed as it is a false positive
        if (parts.length > 1) {
            final JsonObject finalLevel = this.walkthroughBody(newPath);
            element = finalLevel.get(elemToGet);
        } else {
            element = this.jsonBody.get(elemToGet);
        }
        // CHECKSTYLE: FinalLocalVariable ON

        if (element == null) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists!"); // NOPMD
        }

        return element;
    }

    /* ========================== helper ========================= */

    /**
     * Controlla l'esistenza di un elemento evitando di sollevare eccezioni.
     * @param path path dell'elemento.
     * @return <code>true</code> se trovato, <code>false</code> altrimenti.
     */
    @Override
    @SuppressWarnings("unused")
    @SuppressFBWarnings(
        value = "DLS_DEAD_LOCAL_STORE",
        justification = "elem is used only to check existence"
    )
    public boolean elementExists(final String path) {
        try {
            final var elem = this.getFromJson(path);
            return true;
        } catch (final Exception e) { // NOPMD
            return false;
        }
    }

    /* ================== shortcut primitive ================== */

    /**
     * Restituisce una {@link String} reperita dal JSON.
     *
     * @param path path dell'elemento.
     * @return la stringa individuata.
     * @throws IllegalArgumentException se l'elemento non esiste o non è una stringa.
     */
    @Override
    public String getString(final String path) {
        try {
            return this.getFromJson(path, String.class); // NOPMD
        } catch (final Exception e) { // NOPMD
            throw new IllegalArgumentException("\"" + path // NOPMD
            + "\": no such element with this name exists or is not a String!"); // NOPMD
        }
    }

    /**
     * Restituisce un {@link Integer} reperito dal JSON.
     *
     * @param path path dell'elemento.
     * @return il valore intero individuato.
     * @throws IllegalArgumentException se l'elemento non esiste o non è un intero.
     */
    @Override
    public Integer getInt(final String path) {
        try {
            return this.getFromJson(path, Integer.class); // NOPMD
        } catch (final Exception e) { // NOPMD
            throw new IllegalArgumentException("\"" + path // NOPMD
            + "\": no such element with this name exists or is not a Integer!"); // NOPMD
        }
    }

    /**
     * Restituisce un {@link Long} reperito dal JSON.
     *
     * @param path path dell'elemento.
     * @return il valore long individuato.
     * @throws IllegalArgumentException se l'elemento non esiste o non è un long.
     */
    @Override
    public Long getLong(final String path) {
        try {
            return this.getFromJson(path, Long.class); // NOPMD
        } catch (final Exception e) { // NOPMD
            throw new IllegalArgumentException("\"" + path // NOPMD
            + "\": no such element with this name exists or is not a Long!"); // NOPMD
        }
    }

    /**
     * Restituisce un {@link Double} reperito dal JSON.
     *
     * @param path path dell'elemento.
     * @return il valore double individuato.
     * @throws IllegalArgumentException se l'elemento non esiste o non è un double.
     */
    @Override
    public Double getDouble(final String path) {
        try {
            return this.getFromJson(path, Double.class);
        } catch (final Exception e) { // NOPMD
            throw new IllegalArgumentException("\"" + path // NOPMD
            + "\": no such element with this name exists or is not a Double!"); // NOPMD
        }
    }

    /**
     * Restituisce un {@link Float} reperito dal JSON.
     *
     * @param path path dell'elemento.
     * @return il valore float individuato.
     * @throws IllegalArgumentException se l'elemento non esiste o non è un float.
     */
    @Override
    public Float getFloat(final String path) {
        try {
            return this.getFromJson(path, Float.class); // NOPMD
        } catch (final Exception e) { // NOPMD
            throw new IllegalArgumentException("\"" + path // NOPMD
            + "\": no such element with this name exists or is not a Float!"); // NOPMD
        }
    }

    /**
     * Restituisce un valore booleano reperito dal JSON.
     *
     * @param path path dell'elemento.
     * @return il valore booleano individuato.
     * @throws IllegalArgumentException se l'elemento non esiste o non è un booleano.
     */
    @Override
    public boolean getBool(final String path) {
        try { // NOPMD
            return this.getFromJson(path, Boolean.class);
        } catch (final Exception e) { // NOPMD
            throw new IllegalArgumentException("\"" + path // NOPMD
            + "\": no such element with this name exists or is not a boolean!"); // NOPMD
        }
    }

    /* ====================== metodi privati ===================== */

    /**
     * Garantisce che il JSON non sia già stato impostato; altrimenti lancia
     * {@link IllegalStateException} per evitare stati incoerenti.
     */
    private void assertNotAlreadySet() throws IllegalStateException { // NOPMD
        if (this.isSet) {
            throw new IllegalStateException("This AdvancedJsonReader Object was already set!"); // NOPMD
        }
    }

    /** Verifica che il JSON sia stato impostato prima di leggerlo. */
    private void assertIsSet() throws UnsupportedOperationException { // NOPMD
        if (!this.isSet) {
            throw new UnsupportedOperationException("No JSON was set!"); // NOPMD
        }
    }

    /** Effettua il parsing della stringa JSON e marca l'istanza come pronta. */
    private void parseAndSetJson() {
        this.jsonBody = JsonParser.parseString(this.jsonRawText).getAsJsonObject();
        this.isSet = true;
    }

    /**
     * Costruisce il path al livello immediatamente superiore rispetto
     * all'ultimo componente (utility interna a diversi metodi).
     * @param parts
     * array di segmenti di percorso; ogni elemento rappresenta un livello nel percorso completo
     * 
     * @return il percorso di livello superiore come stringa.
     */
    private static String getLevelUpPath(final String[] parts) {
        final StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            newPath.append(parts[i]);
            if (i < parts.length - 2) {
                newPath.append('.');
            }
        }
        return newPath.toString();
    }

}
