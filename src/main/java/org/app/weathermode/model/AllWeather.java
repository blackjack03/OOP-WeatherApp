package org.app.weathermode.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// CHECKSTYLE: AvoidStarImport OFF
import java.util.*;
import java.util.logging.Logger;

import com.google.gson.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;
// CHECKSTYLE: AvoidStarImport ON

/**
 * <h2>AllWeather</h2>
 * <p>Wrapper ad alto livello che interroga l'API <a href="https://open-meteo.com/" target="_blank">Open‑Meteo</a>
 * e aggrega i dati necessari al resto dell'applicazione:</p>
 * <ul>
 *     <li>Previsioni orarie ed estese fino a 8 giorni ({@link #FORECAST_HOURS}).</li>
 *     <li>Riepilogo giornaliero (icona, min/max, UV, ecc.) ({@link #DAILY_GENERAL_FORECAST}).</li>
 *     <li>Informazioni su alba e tramonto per ciascun giorno ({@link #SUN_DAILY_INFO}).</li>
 *     <li>Condizioni correnti con caching smart ({@link #NOW}).</li>
 *     <li>Dati demografici via <em>web‑scraping</em> da ilMeteo.it ({@link #getCityInhabitants(String)}).</li>
 * </ul>
 * <p>La classe implementa l’interfaccia {@link Weather} e si occupa di:</p>
 * <ol>
 *     <li>Costruire le URL partendo dalle coordinate geografiche.</li>
 *     <li>Effettuare la richiesta HTTP tramite {@link AdvancedJsonReaderImpl}.</li>
 *     <li>Normalizzare le unità (°C→°F, mm→inch, km/h→mph).</li>
 *     <li>Esporre comode API <code>Optional&lt;…&gt;</code> per evitare <code>null</code>.</li>
 * </ol>
 * Tutti i metodi pubblici sono <em>thread‑safe</em> a esclusione di una
 * singola istanza concorrente: l’oggetto non è pensato per essere condiviso
 * tra più thread che ne modificano lo stato contemporaneamente.
 */
public class AllWeather implements Weather {

    /* ======================== endpoint ========================== */
    /* ===== previsioni complete 8 giorni ===== */
    private static final String FORECAST_API_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG"
        + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,"
        + "precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m"
        + "&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,"
        + "precipitation_probability,precipitation,weather_code,wind_speed_10m,"
        + "wind_direction_10m,pressure_msl,soil_temperature_0cm"
        + "&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,"
        + "daylight_duration,sunshine_duration,uv_index_max"
        + "&timezone=auto&forecast_days=8";

    /* ===== condizioni correnti ===== */
    private static final String NOW_API_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG"
        + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,"
        + "precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m"
        + "&timezone=auto&forecast_days=1";

    private static final String DETAILS_API_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG"
        + "&minutely_15=precipitation,snowfall,freezing_level_height,weather_code,wind_gusts_10m,visibility"
        + "&start_date=%DATE&end_date=%DATE";

    private static final String URL_CITY_INFO = "https://www.ilmeteo.it/meteo/";

    private static final String WEATHER_CODE_KEY = "weather_code";

    /* ======================= data cache ========================= */
    /** Previsioni orarie per data→ora→metriche. */
    private static final Map<String, Map<String, Map<String, Number>>> FORECAST_HOURS =
            new LinkedHashMap<>();
    /** Riepilogo giornaliero (icona, min/max, etc.). */
    private static final Map<String, Map<String, Number>> DAILY_GENERAL_FORECAST =
            new LinkedHashMap<>();
    /** Sunrise/Sunset per giorno. */
    private static final Map<String, Map<String, String>> SUN_DAILY_INFO =
            new LinkedHashMap<>();
    /** Info città (altitudine, abitanti). */
    private static final Map<String, Number> CITY_INFO = new HashMap<>();
    /** Condizioni correnti. */
    private static final Map<String, Number> NOW = new HashMap<>();

    private static final int REFRESH_TIME = 20;

    /* ==================== variabili di stato ==================== */
    private int forecastDays;
    private String lastDataUpdate = "";  // ISO‑8601 date‑time dell'ultimo now
    private long lastUpdate;  // timestamp epoch seconds
    private boolean requested;

    /* ===================== location & state ===================== */
    private Map<String, String> locationInfo;
    private AbstractPair<String, String> coords;

    /**
     * Costruisce il wrapper e imposta immediatamente la località; i dati non
     * vengono scaricati finché non si invoca {@link #reqestsAllForecast()}.
     *
     * @param locationInfo mappa con chiavi <code>city</code>, <code>lat</code>,
     *                     <code>lng</code>, ecc.
     */
    public AllWeather(final Map<String, String> locationInfo) {
        this.setLocation(locationInfo);
    }

    /* ====================== configurazione ======================= */

    /**
     * Aggiorna la località (e resetta la cache interna).
     *
     * @param locationInfo info della città correntemente selezionata.
     */
    @Override
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Deliberate exposure of a mutable Map to enable external management of location data"
    )
    public final void setLocation(final Map<String, String> locationInfo) {
        this.locationInfo = locationInfo;
        this.coords = new Pair<>(locationInfo.get("lat"), locationInfo.get("lng"));
        this.requested = false;
        this.lastUpdate = 0;
        this.lastDataUpdate = "";
    }

    /* ======================= API principali ===================== */

    /**
     * <p>Scarica <strong>un’unica volta</strong> tutte le previsioni orarie e
     * giornaliere per i prossimi 8 giorni.</p>
     * <p>Il metodo effettua diverse trasformazioni (unità di misura) e popola
     * le strutture dati interne. In caso di qualunque eccezione restituisce
     * <code>false</code> per consentire al chiamante di gestire il fallimento
     * senza lanciare unchecked exception.</p>
     *
     * @return <code>true</code> se il download/parsing è andato a buon fine.
     */
    @Override
    public boolean reqestsAllForecast() {
        try {
            /* ================= download & parse ================= */
            final var reader = new AdvancedJsonReaderImpl(FORECAST_API_URL
                    .replace("%LAT", this.coords.getX())
                    .replace("%LNG", this.coords.getY()));

            /* ========== informazioni statiche sulla città ======= */
            CITY_INFO.put("meters_above_sea", reader.getFloat("elevation"));
            final var inhab = this.getCityInhabitants(this.locationInfo.get("city_ascii"));
            CITY_INFO.put("inhabitants", inhab.orElse(null));

            /* ========== previsioni orarie per 8 giorni =========== */
            final var datesHours = reader.getJsonArray("hourly.time");
            final var temperature2m = reader.getJsonArray("hourly.temperature_2m");
            final var humidity2m = reader.getJsonArray("hourly.relative_humidity_2m");
            final var apparentTemperature = reader.getJsonArray("hourly.apparent_temperature");
            final var precipitationProbability = reader.getJsonArray("hourly.precipitation_probability");
            final var precipitation = reader.getJsonArray("hourly.precipitation");
            final var weatherCode = reader.getJsonArray("hourly.weather_code");
            final var windSpeed10m = reader.getJsonArray("hourly.wind_speed_10m");
            final var windDirection10m = reader.getJsonArray("hourly.wind_direction_10m");
            final var soilTemperature0cm = reader.getJsonArray("hourly.soil_temperature_0cm");
            final var pressureMsl = reader.getJsonArray("hourly.pressure_msl");

            for (int i = 0; i < datesHours.size(); i++) {
                final String[] dateTime = datesHours.get(i).getAsString().split("T");
                final String dateKey = dateTime[0];
                final String hourKey = dateTime[1].split(":")[0];

                FORECAST_HOURS.computeIfAbsent(dateKey, k -> new HashMap<>());

                final Map<String, Number> info = new HashMap<>();
                info.put("temperature_C", temperature2m.get(i).getAsNumber());
                info.put("temperature_F", UnitConversion.celsiusToFahrenheit(temperature2m.get(i).getAsDouble()));
                info.put("humidity", humidity2m.get(i).getAsNumber());
                info.put("apparent_temperature", apparentTemperature.get(i).getAsNumber());
                info.put("precipitation_probability", precipitationProbability.get(i).getAsNumber());
                info.put("precipitation_mm", precipitation.get(i).getAsNumber());
                info.put("precipitation_inch", UnitConversion.mmToInches(precipitation.get(i).getAsDouble()));
                info.put(WEATHER_CODE_KEY, weatherCode.get(i).getAsNumber());
                info.put("wind_speed_kmh", windSpeed10m.get(i).getAsNumber());
                info.put("wind_speed_mph", UnitConversion.kmhToMph(windSpeed10m.get(i).getAsDouble()));
                info.put("wind_direction", windDirection10m.get(i).getAsNumber());
                info.put("pressure", pressureMsl.get(i).getAsNumber());
                info.put("soil_temperature", soilTemperature0cm.get(i).isJsonNull()
                    ? null : soilTemperature0cm.get(i).getAsNumber());

                FORECAST_HOURS.get(dateKey).put(hourKey, info);
            }
            this.forecastDays = FORECAST_HOURS.size();

            /* ========== previsioni giornaliere riassuntive ======= */
            final var days = reader.getJsonArray("daily.time");
            final var dayWeatherCode = reader.getJsonArray("daily.weather_code");
            final var temperature2mMax = reader.getJsonArray("daily.temperature_2m_max");
            final var temperature2mMin = reader.getJsonArray("daily.temperature_2m_min");
            final var daylightDuration = reader.getJsonArray("daily.daylight_duration");
            final var sunshineDuration = reader.getJsonArray("daily.sunshine_duration");
            final var uvIndexMax = reader.getJsonArray("daily.uv_index_max");
            final var daysSunrise = reader.getJsonArray("daily.sunrise");
            final var daysSunset = reader.getJsonArray("daily.sunset");
            for (int i = 0; i < days.size(); i++) {
                final String keyDay = days.get(i).getAsString();
                final Map<String, Number> forecastInfo = new HashMap<>();
                forecastInfo.put(WEATHER_CODE_KEY, dayWeatherCode.get(i).getAsNumber());
                forecastInfo.put("temperature_max_C", temperature2mMax.get(i).getAsNumber());
                forecastInfo.put("temperature_max_F", UnitConversion.celsiusToFahrenheit(temperature2mMax.get(i).getAsDouble()));
                forecastInfo.put("temperature_min_C", temperature2mMin.get(i).getAsNumber());
                forecastInfo.put("temperature_min_F", UnitConversion.celsiusToFahrenheit(temperature2mMin.get(i).getAsDouble()));
                forecastInfo.put("daylight_duration", daylightDuration.get(i).getAsNumber());
                forecastInfo.put("sunshine_duration", sunshineDuration.get(i).getAsNumber());
                forecastInfo.put("uv_max", uvIndexMax.get(i).getAsNumber());
                DAILY_GENERAL_FORECAST.put(keyDay, forecastInfo);

                final Map<String, String> otherInfo = new HashMap<>();
                otherInfo.put("sunrise", daysSunrise.get(i).getAsString().split("T")[1]);
                otherInfo.put("sunset", daysSunset.get(i).getAsString().split("T")[1]);
                SUN_DAILY_INFO.put(keyDay, otherInfo);
            }

            /* ========== condizioni correnti ====================== */
            if (!this.setCurrentWeather(reader)) {
                return false;
            }

            this.requested = true;
            return true;
        } catch (final Exception err) { // NOPMD
            return false;
        }
    }

    /**
     * Restituisce un <code>Map</code> con i parametri meteo del momento
     * indicato (precisione 15‑minuti) effettuando una richiesta puntuale.
     *
     * @param day   giorno (1‑31).
     * @param month mese (1‑12).
     * @param year  anno a quattro cifre.
     * @param hour  orario <code>HH:mm</code> da arrotondare al quarto d’ora più vicino.
     * @return dati meteo o {@link Optional#empty()} in caso di errore.
     */
    @Override
    @SuppressFBWarnings(
        value = "REC_CATCH_EXCEPTION", // NOPMD
        justification = "Necessary to catch generic Exception to aggregate parsing errors from AdvancedJsonReader" // NOPMD
    )
    public Optional<Map<String, Number>> getWeatherOn(final int day, final int month, final int year, final String hour) {
        try {
            final String nearHour = this.roundToNearestQuarter(hour);
            final String date = String.format("%04d-%02d-%02d", year, month, day);
            final String dateHour = date + "T" + nearHour;

            final var reader = new AdvancedJsonReaderImpl(DETAILS_API_URL
                .replace("%LAT", this.coords.getX())
                .replace("%LNG", this.coords.getY())
                .replace("%DATE", date));

            final JsonArray minues = reader.getJsonArray("minutely_15.time");
            final int idx = jsonArrayIndexOf(minues, dateHour);
            if (idx < 0) {
                return Optional.empty();
            }

            final Map<String, Number> out = new HashMap<>();
            out.put("precipitation", reader.getJsonArray("minutely_15.precipitation").get(idx).getAsNumber());
            out.put("snowfall", reader.getJsonArray("minutely_15.snowfall").get(idx).getAsNumber());
            out.put("freezing_level_height", reader.getJsonArray("minutely_15.freezing_level_height").get(idx).getAsNumber());
            out.put(WEATHER_CODE_KEY, reader.getJsonArray("minutely_15.weather_code").get(idx).getAsNumber());
            out.put("wind_gusts", reader.getJsonArray("minutely_15.wind_gusts_10m").get(idx).getAsNumber());
            out.put("visibility", reader.getJsonArray("minutely_15.visibility").get(idx).getAsNumber());
            return Optional.of(out);
        } catch (final Exception e) { // NOPMD
            return Optional.empty();
        }
    }

    /* ==================== getter & helper ==================== */

    /**
     * Restituisce il numero di giorni per cui sono disponibili le previsioni.
     *
     * @return il numero di giorni di previsione (fino a 8 giorni).
     */
    @Override
    public int getForecastDays() {
        return this.forecastDays;
    }

    /**
     * Ottiene tutte le previsioni orarie per ciascun giorno.
     * <p>
     * Restituisce una mappa struttura come:
     * <pre>
     *  data (YYYY-MM-DD) → ora (HH) → metrica → valore
     * </pre>
     * Usare {@code Optional.empty()} se {@link #reqestsAllForecast()} non è stato invocato con successo.
     *
     * @return un {@code Optional} contenente la mappa delle previsioni orarie, o vuoto se non disponibili.
     */
    @Override
    public Optional<Map<String, Map<String, Map<String, Number>>>> getAllForecast() {
        return this.requested
            ? Optional.of(FORECAST_HOURS)
            : Optional.empty();
    }

    /**
     * Ottiene il riepilogo giornaliero delle previsioni.
     * <p>
     * Restituisce una mappa struttura come:
     * <pre>
     *  data (YYYY-MM-DD) → chiave metrica (es. "temperature_max_C", "uv_max", ...) → valore
     * </pre>
     * Usare {@code Optional.empty()} se {@link #reqestsAllForecast()} non è stato invocato con successo.
     *
     * @return un {@code Optional} contenente la mappa del riepilogo giornaliero, o vuoto se non disponibile.
     */
    @Override
    public Optional<Map<String, Map<String, Number>>> getDailyGeneralForecast() {
        return this.requested
            ? Optional.of(DAILY_GENERAL_FORECAST)
            : Optional.empty();
    }

    /**
     * Ottiene le informazioni di alba e tramonto per ciascun giorno.
     * <p>
     * Restituisce una mappa struttura come:
     * <pre>
     *  data (YYYY-MM-DD) → {"sunrise" → HH:mm, "sunset" → HH:mm}
     * </pre>
     * Usare {@code Optional.empty()} se {@link #reqestsAllForecast()} non è stato invocato con successo.
     *
     * @return un {@code Optional} contenente la mappa delle informazioni di alba/tramonto, o vuoto se non disponibile.
     */
    @Override
    public Optional<Map<String, Map<String, String>>> getDailyInfo() {
        return this.requested
            ? Optional.of(SUN_DAILY_INFO)
            : Optional.empty();
    }

    /**
     * Dati meteo correnti (con caching 20 minuti).
     *
     * @param avoidCheck se <code>true</code> ignora il cache‑timeout.
     * @return una {@code Pair} contenente l'ISO‑datetime dell'ultimo aggiornamento e la mappa {@link #NOW} con i dati correnti.
     */
    @Override
    @SuppressFBWarnings(
        value = "REC_CATCH_EXCEPTION",
        justification = "Necessary to catch generic Exception to aggregate parsing errors from AdvancedJsonReader"
    )
    public Optional<Pair<String, Map<String, Number>>> getWeatherNow(final boolean avoidCheck) {
        if (this.lastUpdate == 0 || avoidCheck
            || this.checkMinutesPassed(this.lastUpdate, REFRESH_TIME)) {
            try {
                final var reader = new AdvancedJsonReaderImpl(NOW_API_URL
                    .replace("%LAT", this.coords.getX())
                    .replace("%LNG", this.coords.getY()));
                if (!this.setCurrentWeather(reader)) {
                    this.lastUpdate = 0; // NOPMD false positive
                    return Optional.empty();
                }
            } catch (final Exception err) { // NOPMD
                this.lastUpdate = 0;
                return Optional.empty();
            }
        }
        return Optional.of(new Pair<>(this.lastDataUpdate, NOW));
    }

    /**
     * Ritorna le informazioni relative alla città, se presenti.
     * 
     * @return un {@code Optional} con le informazioni delle città.
     */
    @Override
    public Optional<Map<String, Number>> getCityInfo() {
        return this.lastUpdate == 0
        ? Optional.empty() : Optional.of(CITY_INFO);
    }

    /**
     * Converte gradi bussola (0‑360) in direzione testuale.
     *
     * @param degrees valore in gradi da convertire.
     * @return la stringa corrispondente alla direzione del vento (es. "Nord", "Nordest", ...).
     */
    // CHECKSTYLE: MagicNumber OFF
    @Override
    public String getWindDirection(final int degrees) {
        final int maxDegrees = 360;
        final List<String> direzioni = Arrays.asList(
            "Nord", "Nord-Nordest", "Nordest", "Est-Nordest",
            "Est", "Est-Sudest", "Sudest", "Sud-Sudest",
            "Sud", "Sud-Sudoest", "Sudoest", "Ovest-Sudoest",
            "Ovest", "Ovest-Nord-Ovest", "Nord-Ovest", "Nord-Nord-Ovest"
        );
        // CHECKSTYLE: FinalLocalVariable OFF
        int degr; // NOPMD
        degr = (degrees % maxDegrees + maxDegrees) % maxDegrees; // normalizza
        final double degrToAdj = 11.25;
        double adjustedDegrees = degr + degrToAdj;
        if (adjustedDegrees >= maxDegrees) {
            adjustedDegrees -= maxDegrees;
        }
        // CHECKSTYLE: FinalLocalVariable ON
        return direzioni.get((int) (adjustedDegrees / 22.5) % 16);
    }
    // CHECKSTYLE: MagicNumber ON

    /* ===================== metodi privati ===================== */

    /**
     * Riempie la mappa {@link #NOW} con i campi restituiti dal reader.
     * Aggiorna anche <strong>timestamp cache</strong> e stringa ISO di update.
     *
     * @param reader reader JSON avanzato che fornisce i dati correnti.
     * @return <code>true</code> se tutti i campi essenziali sono presenti.
     */
    @SuppressFBWarnings(
        value = "REC_CATCH_EXCEPTION",
        justification = "Necessary to catch generic Exception to aggregate parsing errors from AdvancedJsonReader"
    )
    private boolean setCurrentWeather(final AdvancedJsonReader reader) { // NOPMD
        try {
            this.lastUpdate = System.currentTimeMillis() / 1000L;
            this.lastDataUpdate = reader.getString("current.time");
            NOW.put(WEATHER_CODE_KEY, reader.getFromJson("current.weather_code", Number.class));
            NOW.put("temperature_C", reader.getFromJson("current.temperature_2m", Number.class));
            NOW.put("temperature_F", UnitConversion.celsiusToFahrenheit(reader.getDouble("current.temperature_2m")));
            NOW.put("apparent_temperature_C", reader.getFromJson("current.apparent_temperature", Number.class));
            NOW.put("apparent_temperature_F",
                UnitConversion.celsiusToFahrenheit(reader.getDouble("current.apparent_temperature")));
            NOW.put("humidity", reader.getFromJson("current.relative_humidity_2m", Number.class));
            NOW.put("wind_speed_kmh", reader.getFromJson("current.wind_speed_10m", Number.class));
            NOW.put("wind_speed_mph", UnitConversion.kmhToMph(reader.getDouble("current.wind_speed_10m")));
            NOW.put("wind_direction", reader.getFromJson("current.wind_direction_10m", Number.class));
            NOW.put("precipitation_mm", reader.getFromJson("current.precipitation", Number.class));
            NOW.put("precipitation_inch", UnitConversion.mmToInches(reader.getDouble("current.precipitation")));
            NOW.put("cloud_cover", reader.getFromJson("current.cloud_cover", Number.class));
            return true;
        } catch (final Exception err) { // NOPMD
            return false;
        }
    }

    /**
     * Verifica se sono trascorsi almeno <code>min</code> minuti dal timestamp.
     *
     * @param timestamp epoch seconds di riferimento.
     * @param min       minuti da verificare.
     * @return <code>true</code> se sono trascorsi almeno <code>min</code> minuti, altrimenti <code>false</code>.
     */
    // CHECKSTYLE: MagicNumber OFF
    private boolean checkMinutesPassed(final long timestamp, final int min) {
        final long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        return (currentTimeInSeconds - timestamp) >= min * 60L;
    }
    // CHECKSTYLE: MagicNumber ON

    /**
     * Scraping su ilMeteo.it per recuperare il numero di abitanti della città.
     *
     * @param asciiCityName nome ASCII della città.
     * @return <code>Optional.empty()</code> se la pagina non è strutturata come previsto.
     */
    @SuppressFBWarnings(
        value = "REC_CATCH_EXCEPTION",
        justification = "Necessary to catch generic Exception to aggregate parsing errors from AdvancedJsonReader"
    )
    private Optional<Integer> getCityInhabitants(final String asciiCityName) {
        try {
            final Document doc = Jsoup.connect(URL_CITY_INFO + asciiCityName).get();
            final Elements info = doc.getElementsByClass("infoloc"); // NOPMD
            if (!info.isEmpty()) {
                final Matcher matcher = Pattern.compile("([\\d.]+)\\s*abitanti").matcher(info.get(0).text());
                if (matcher.find()) {
                    return Optional.of(strToInt(matcher.group(1)));
                }
            }
        } catch (final Exception ignored) { // NOPMD
            Logger.getLogger(AllWeather.class.getName())
                .fine("Numero abitanti non disponibile per questa città (%s).".formatted(asciiCityName));
        }
        return Optional.empty();
    }

    /**
     * Rimuove separatori (punto/spazio) e converte in <code>int</code>.
     *
     * @param str stringa numerica da convertire.
     * @return il valore intero corrispondente.
     */
    private int strToInt(final String str) {
        return Integer.parseInt(str.replaceAll("[^\\d]", ""));
    }

    /**
     * Arrotonda l'orario al quarto d'ora più vicino (HH:mm).
     *
     * @param orario orario di partenza nel formato HH:mm.
     * @return la stringa oraria arrotondata al quarto d'ora più vicino.
     */
    // CHECKSTYLE: MagicNumber OFF
    private String roundToNearestQuarter(final String orario) {
        final String[] parti = orario.split(":");
        int ore = Integer.parseInt(parti[0]);
        int minuti = Integer.parseInt(parti[1]);
        final int resto = minuti % 15;
        minuti = resto < 8 ? minuti - resto : minuti + 15 - resto;
        if (minuti == 60) {
            minuti = 0;
            ore = (ore + 1) % 24;
        }
        return String.format("%02d:%02d", ore, minuti);
    }
    // CHECKSTYLE: MagicNumber ON

    /**
     * Restituisce l'indice di un valore String all'interno di un {@link JsonArray}.
     *
     * @param array  l'array JSON da scansionare.
     * @param target il valore da cercare.
     * @return torna un intero, l'indice corrispondente al primo elmento uguale o -1 se il target non viene trovato.
     */
    private static int jsonArrayIndexOf(final JsonArray array, final String target) {
        for (int i = 0; i < array.size(); i++) {
            if (target.equals(array.get(i).getAsString())) {
                return i;
            }
        }
        return -1;
    }

}
