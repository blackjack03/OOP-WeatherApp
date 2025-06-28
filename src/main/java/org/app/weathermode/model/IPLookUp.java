package org.app.weathermode.model;

import java.util.Optional;

/**
 * <h2>IPLookUp</h2>
 * <p>Servizio di <strong>geo‑localizzazione IP</strong> che interroga l’endpoint
 * <a href="https://api.codetabs.com/v1/geolocation/json">Codetabs Geolocation API</a>
 * e mappa la risposta JSON in un set di proprietà ad uso dell’applicazione
 * (es. per scegliere automaticamente la città “di partenza” alla prima
 * installazione).</p>
 *
 * <p>Il contratto è definito dall’interfaccia {@link LookUp}. Alla prima
 * invocazione di {@link #lookup()} il metodo tenta fino a
 * {@value #MAX_ATTEMPTS} volte l’HTTP GET, restituendo
 * {@link Optional#empty()} se tutti i tentativi falliscono; in caso di successo
 * le informazioni vengono cache‑ate nell’istanza e rese disponibili tramite
 * i rispettivi <code>get*</code>.</p>
 */
public class IPLookUp implements LookUp {

    /* ======================== costanti ======================== */
    /** Endpoint JSON pubblico senza autenticazione. */
    private static final String API_URL = "https://api.codetabs.com/v1/geolocation/json";
    /** Numero massimo di retry in {@link #lookup()}. */
    private static final int MAX_ATTEMPTS = 10;

    /* ======================== stato =========================== */
    private String ip = "";
    private String countryCode = "";
    private String countryName = "";
    private String regionName = "";
    private String city = "";
    private String zipCode = "";
    private String timeZone = "";
    private Pair<Double, Double> coords = null; // lat, lng

    /* ========================= ctor =========================== */

    /** Costruttore vuoto: la lookup vera e propria avviene su {@link #lookup()}. */
    public IPLookUp() { /* empty body */ }

    /* ===================== API LookUp ========================= */

    /**
     * Effettua la richiesta di localizzazione. Tutti i campi precedenti vengono
     * azzerati tramite {@link #clear()} prima di ogni tentativo.
     *
     * @return {@link Optional#of(Boolean.TRUE)} se almeno una richiesta ha avuto
     *         successo; {@link Optional#empty()} in caso contrario.
     */
    @Override
    public Optional<Boolean> lookup() {
        this.clear();
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            if (this.doLookUpReq()) {
                return Optional.of(Boolean.TRUE);
            }
        }
        return Optional.empty();
    }

    /* ==================== getter semplici ===================== */

    @Override public String getIP()          { return this.ip; }
    @Override public String getCountryCode() { return this.countryCode; }
    @Override public String getCountry()     { return this.countryName; }
    @Override public String getRegion()      { return this.regionName; }
    @Override public String getCity()        { return this.city; }
    @Override public String getZipCode()     { return this.zipCode; }
    @Override public String getTimeZone()    { return this.timeZone; }
    @Override public Pair<Double, Double> getCoords() { return this.coords; }

    /* ======================== debug =========================== */

    /**
     * @return stringa leggibile con tutti i campi ottenuti dalla lookup.
     */
    @Override
    public String toString() {
        return "IPLookUp{" +
                "ip='" + ip + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", coords=" + coords +
                '}';
    }

    /* ==================== helper privati ===================== */

    /** Reimposta tutti i campi prima di un nuovo tentativo di lookup. */
    private void clear() {
        this.ip = this.countryCode = this.countryName = this.regionName = "";
        this.city = this.zipCode = this.timeZone = "";
        this.coords = null;
    }

    /**
     * Implementa la singola chiamata HTTP e il mapping JSON→campi.
     *
     * @return <code>true</code> se il parsing e l’assegnazione vanno a buon fine.
     */
    private boolean doLookUpReq() {
        try {
            final AdvancedJsonReader ipinfo = new AdvancedJsonReaderImpl(API_URL);
            this.ip = ipinfo.getString("ip");
            this.countryCode = ipinfo.getString("country_code");
            this.countryName = ipinfo.getString("country_name");
            this.regionName = ipinfo.getString("region_name");
            this.city = ipinfo.getString("city");
            this.zipCode = ipinfo.getString("zip_code");
            this.timeZone = ipinfo.getString("time_zone");
            this.coords = new Pair<>(ipinfo.getDouble("latitude"), ipinfo.getDouble("longitude"));
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

}
