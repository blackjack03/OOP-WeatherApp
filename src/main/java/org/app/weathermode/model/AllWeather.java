package org.app.weathermode.model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;

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

    /* ===================== location & state ===================== */
    private Map<String, String> locationInfo;
    private AbstractPair<String, String> coords;

    /* ======================== endpoint ========================== */
    private final String FORECAST_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,wind_speed_10m,wind_direction_10m,pressure_msl,soil_temperature_0cm&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,sunshine_duration,uv_index_max&timezone=auto&forecast_days=8";
    private final String NOW_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m&timezone=auto&forecast_days=1";
    private final String DETAILS_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG&minutely_15=precipitation,snowfall,freezing_level_height,weather_code,wind_gusts_10m,visibility&start_date=%DATE&end_date=%DATE";
    private final String URL_CITY_INFO = "https://www.ilmeteo.it/meteo/";

    /* ======================= data cache ========================= */
    /** Previsioni orarie per data→ora→metriche. */
    private final Map<String, Map<String, Map<String, Number>>> FORECAST_HOURS =
            new LinkedHashMap<>();
    /** Riepilogo giornaliero (icona, min/max, etc.). */
    private final Map<String, Map<String, Number>> DAILY_GENERAL_FORECAST =
            new LinkedHashMap<>();
    /** Sunrise/Sunset per giorno. */
    private final Map<String, Map<String, String>> SUN_DAILY_INFO =
            new LinkedHashMap<>();
    /** Info città (altitudine, abitanti). */
    private final Map<String, Number> CITY_INFO = new HashMap<>();
    /** Condizioni correnti. */
    private final Map<String, Number> NOW = new HashMap<>();

    /* ==================== variabili di stato ==================== */
    private int forecast_days = 0;
    private String lastDataUpdate = "";  // ISO‑8601 date‑time dell'ultimo now
    private long last_update = 0;         // timestamp epoch seconds
    private boolean requested = false;    // true dopo la prima fetch globale

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
    public void setLocation(final Map<String, String> locationInfo) {
        this.locationInfo = locationInfo;
        this.coords = new Pair<>(locationInfo.get("lat"), locationInfo.get("lng"));
        this.requested = false;
        this.last_update = 0;
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
            this.CITY_INFO.put("meters_above_sea", reader.getFloat("elevation"));
            final var inhab = this.getCityInhabitants(this.locationInfo.get("city_ascii"));
            this.CITY_INFO.put("inhabitants", inhab.orElse(null));

            /* ========== previsioni orarie per 8 giorni =========== */
            final var dates_hours = reader.getJsonArray("hourly.time");
            final var temperature_2m = reader.getJsonArray("hourly.temperature_2m");
            final var humidity_2m = reader.getJsonArray("hourly.relative_humidity_2m");
            final var apparent_temperature = reader.getJsonArray("hourly.apparent_temperature");
            final var precipitation_probability = reader.getJsonArray("hourly.precipitation_probability");
            final var precipitation = reader.getJsonArray("hourly.precipitation");
            final var weather_code = reader.getJsonArray("hourly.weather_code");
            final var wind_speed_10m = reader.getJsonArray("hourly.wind_speed_10m");
            final var wind_direction_10m = reader.getJsonArray("hourly.wind_direction_10m");
            final var soil_temperature_0cm = reader.getJsonArray("hourly.soil_temperature_0cm");
            final var pressure_msl = reader.getJsonArray("hourly.pressure_msl");

            for (int i = 0; i < dates_hours.size(); i++) {
                final String[] dateTime = dates_hours.get(i).getAsString().split("T");
                final String dateKey = dateTime[0];
                final String hourKey = dateTime[1].split(":" )[0];

                FORECAST_HOURS.computeIfAbsent(dateKey, k -> new HashMap<>());

                final Map<String, Number> info = new HashMap<>();
                info.put("temperature_C", temperature_2m.get(i).getAsNumber());
                info.put("temperature_F", UnitConversion.celsiusToFahrenheit(temperature_2m.get(i).getAsDouble()));
                info.put("humidity", humidity_2m.get(i).getAsNumber());
                info.put("apparent_temperature", apparent_temperature.get(i).getAsNumber());
                info.put("precipitation_probability", precipitation_probability.get(i).getAsNumber());
                info.put("precipitation_mm", precipitation.get(i).getAsNumber());
                info.put("precipitation_inch", UnitConversion.mmToInches(precipitation.get(i).getAsDouble()));
                info.put("weather_code", weather_code.get(i).getAsNumber());
                info.put("wind_speed_kmh", wind_speed_10m.get(i).getAsNumber());
                info.put("wind_speed_mph", UnitConversion.kmhToMph(wind_speed_10m.get(i).getAsDouble()));
                info.put("wind_direction", wind_direction_10m.get(i).getAsNumber());
                info.put("pressure", pressure_msl.get(i).getAsNumber());
                info.put("soil_temperature", soil_temperature_0cm.get(i).isJsonNull() ? null : soil_temperature_0cm.get(i).getAsNumber());

                FORECAST_HOURS.get(dateKey).put(hourKey, info);
            }
            this.forecast_days = FORECAST_HOURS.size();

            /* ========== previsioni giornaliere riassuntive ======= */
            final var days = reader.getJsonArray("daily.time");
            final var day_weather_code = reader.getJsonArray("daily.weather_code");
            final var temperature_2m_max = reader.getJsonArray("daily.temperature_2m_max");
            final var temperature_2m_min = reader.getJsonArray("daily.temperature_2m_min");
            final var daylight_duration = reader.getJsonArray("daily.daylight_duration");
            final var sunshine_duration = reader.getJsonArray("daily.sunshine_duration");
            final var uv_index_max = reader.getJsonArray("daily.uv_index_max");
            final var days_sunrise = reader.getJsonArray("daily.sunrise");
            final var days_sunset = reader.getJsonArray("daily.sunset");
            for (int i = 0; i < days.size(); i++) {
                final String KEY_DAY = days.get(i).getAsString();
                final Map<String, Number> forecast_info = new HashMap<>();
                forecast_info.put("weather_code", day_weather_code.get(i).getAsNumber());
                forecast_info.put("temperature_max_C", temperature_2m_max.get(i).getAsNumber());
                forecast_info.put("temperature_max_F", UnitConversion.celsiusToFahrenheit(temperature_2m_max.get(i).getAsDouble()));
                forecast_info.put("temperature_min_C", temperature_2m_min.get(i).getAsNumber());
                forecast_info.put("temperature_min_F", UnitConversion.celsiusToFahrenheit(temperature_2m_min.get(i).getAsDouble()));
                forecast_info.put("daylight_duration", daylight_duration.get(i).getAsNumber());
                forecast_info.put("sunshine_duration", sunshine_duration.get(i).getAsNumber());
                forecast_info.put("uv_max", uv_index_max.get(i).getAsNumber());
                DAILY_GENERAL_FORECAST.put(KEY_DAY, forecast_info);

                final Map<String, String> other_info = new HashMap<>();
                other_info.put("sunrise", days_sunrise.get(i).getAsString().split("T")[1]);
                other_info.put("sunset", days_sunset.get(i).getAsString().split("T")[1]);
                SUN_DAILY_INFO.put(KEY_DAY, other_info);
            }

            /* ========== condizioni correnti ====================== */
            if (!this.setCurrentWeather(reader)) {
                return false;
            }

            this.requested = true;
            return true;
        } catch (final Exception err) {
            err.printStackTrace();
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
    public Optional<Map<String, Number>> getWeatherOn(final int day, final int month, final int year, final String hour) {
        try {
            final String nearHour = this.roundToNearestQuarter(hour);
            final String DATE = String.format("%04d-%02d-%02d", year, month, day);
            final String DATE_HOUR = DATE + "T" + nearHour;

            final var reader = new AdvancedJsonReaderImpl(DETAILS_API_URL
                .replace("%LAT", this.coords.getX())
                .replace("%LNG", this.coords.getY())
                .replace("%DATE", DATE));

            final JsonArray MINUTES = reader.getJsonArray("minutely_15.time");
            final int IDX = jsonArrayIndexOf(MINUTES, DATE_HOUR);
            if (IDX < 0) return Optional.empty();

            final Map<String, Number> OUT = new HashMap<>();
            OUT.put("precipitation", reader.getJsonArray("minutely_15.precipitation").get(IDX).getAsNumber());
            OUT.put("snowfall", reader.getJsonArray("minutely_15.snowfall").get(IDX).getAsNumber());
            OUT.put("freezing_level_height", reader.getJsonArray("minutely_15.freezing_level_height").get(IDX).getAsNumber());
            OUT.put("weather_code", reader.getJsonArray("minutely_15.weather_code").get(IDX).getAsNumber());
            OUT.put("wind_gusts", reader.getJsonArray("minutely_15.wind_gusts_10m").get(IDX).getAsNumber());
            OUT.put("visibility", reader.getJsonArray("minutely_15.visibility").get(IDX).getAsNumber());
            return Optional.of(OUT);
        } catch(final Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /* ==================== getter & helper ==================== */

    @Override
    public int getForecastDays() { return this.forecast_days; }
    @Override
    public Optional<Map<String, Map<String, Map<String, Number>>>> getAllForecast() { return this.requested ?
        Optional.of(this.FORECAST_HOURS) : Optional.empty(); }
    @Override
    public Optional<Map<String, Map<String, Number>>> getDailyGeneralForecast() { return this.requested ?
        Optional.of(this.DAILY_GENERAL_FORECAST) : Optional.empty(); }
    @Override
    public Optional<Map<String, Map<String, String>>> getDailyInfo() { return this.requested ?
        Optional.of(this.SUN_DAILY_INFO) : Optional.empty(); }

    /**
     * Dati meteo correnti (con caching 20 minuti).
     * @param avoid_check se <code>true</code> ignora il cache‑timeout.
     */
    @Override
    public Optional<Pair<String, Map<String, Number>>> getWeatherNow(final boolean avoid_check) {
        final int refreshTime = 20;
        if (this.last_update == 0 || avoid_check ||
                this.checkMinutesPassed(this.last_update, refreshTime)) {
            try {
                final var reader = new AdvancedJsonReaderImpl(this.NOW_API_URL
                    .replace("%LAT", this.coords.getX())
                    .replace("%LNG", this.coords.getY()));
                if (!this.setCurrentWeather(reader)) {
                    this.last_update = 0;
                    return Optional.empty();
                }
            } catch (final Exception err) {
                this.last_update = 0;
                err.printStackTrace();
                return Optional.empty();
            }
        }
        return Optional.of(new Pair<>(this.lastDataUpdate, this.NOW));
    }

    @Override
    public Optional<Map<String, Number>> getCityInfo() { return this.last_update == 0 ? Optional.empty() : Optional.of(this.CITY_INFO); }

    /**
     * Converte gradi bussola (0‑360) in direzione testuale.
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public String getWindDirection(int degrees) {
        final List<String> DIREZIONI = Arrays.asList(
            "Nord", "Nord-Nordest", "Nordest", "Est-Nordest",
            "Est", "Est-Sudest", "Sudest", "Sud-Sudest",
            "Sud", "Sud-Sudoest", "Sudoest", "Ovest-Sudoest",
            "Ovest", "Ovest-Nord-Ovest", "Nord-Ovest", "Nord-Nord-Ovest"
        );
        degrees = (degrees % 360 + 360) % 360; // normalizza
        double adjustedDegrees = degrees + 11.25;
        if (adjustedDegrees >= 360) adjustedDegrees -= 360;
        return DIREZIONI.get((int)(adjustedDegrees / 22.5) % 16);
    }

    /* ===================== metodi privati ===================== */

    /**
     * Riempie la mappa {@link #NOW} con i campi restituiti dal reader.
     * Aggiorna anche <strong>timestamp cache</strong> e stringa ISO di update.
     *
     * @return <code>true</code> se tutti i campi essenziali sono presenti.
     */
    private boolean setCurrentWeather(final AdvancedJsonReader reader) {
        try {
            this.last_update = System.currentTimeMillis() / 1000L;
            this.lastDataUpdate = reader.getString("current.time");
            this.NOW.put("weather_code", reader.getFromJson("current.weather_code", Number.class));
            this.NOW.put("temperature_C", reader.getFromJson("current.temperature_2m", Number.class));
            this.NOW.put("temperature_F", UnitConversion.celsiusToFahrenheit(reader.getDouble("current.temperature_2m")));
            this.NOW.put("apparent_temperature_C", reader.getFromJson("current.apparent_temperature", Number.class));
            this.NOW.put("apparent_temperature_F", UnitConversion.celsiusToFahrenheit(reader.getDouble("current.apparent_temperature")));
            this.NOW.put("humidity", reader.getFromJson("current.relative_humidity_2m", Number.class));
            this.NOW.put("wind_speed_kmh", reader.getFromJson("current.wind_speed_10m", Number.class));
            this.NOW.put("wind_speed_mph", UnitConversion.kmhToMph(reader.getDouble("current.wind_speed_10m")));
            this.NOW.put("wind_direction", reader.getFromJson("current.wind_direction_10m", Number.class));
            this.NOW.put("precipitation_mm", reader.getFromJson("current.precipitation", Number.class));
            this.NOW.put("precipitation_inch", UnitConversion.mmToInches(reader.getDouble("current.precipitation")));
            this.NOW.put("cloud_cover", reader.getFromJson("current.cloud_cover", Number.class));
            return true;
        } catch (final Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica se sono trascorsi almeno <code>min</code> minuti dal timestamp.
     */
    private boolean checkMinutesPassed(final long timestamp, final int min) {
        final long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        return (currentTimeInSeconds - timestamp) >= min * 60L;
    }

    /**
     * Scraping su ilMeteo.it per recuperare il numero di abitanti della città.
     * @return <code>Optional.empty()</code> se la pagina non è strutturata come previsto.
     */
    private Optional<Integer> getCityInhabitants(final String asciiCityName) {
        try {
            final Document doc = Jsoup.connect(this.URL_CITY_INFO + asciiCityName).get();
            final Elements info = doc.getElementsByClass("infoloc");
            if (!info.isEmpty()) {
                final Matcher matcher = Pattern.compile("([\\d.]+)\\s*abitanti").matcher(info.get(0).text());
                if (matcher.find()) {
                    return Optional.of(strToInt(matcher.group(1)));
                }
            }
        } catch (final Exception ignored) {}
        return Optional.empty();
    }

    /** Rimuove separatori (punto/spazio) e converte in <code>int</code>. */
    private int strToInt(final String str) {
        return Integer.parseInt(str.replaceAll("[^\\d]", ""));
    }

    /** Arrotonda l'orario al quarto d'ora più vicino (HH:mm). */
    private String roundToNearestQuarter(final String orario) {
        final String[] parti = orario.split(":" );
        int ore = Integer.parseInt(parti[0]);
        int minuti = Integer.parseInt(parti[1]);
        int resto = minuti % 15;
        minuti = resto < 8 ? minuti - resto : minuti + (15 - resto);
        if (minuti == 60) {
            minuti = 0;
            ore = (ore + 1) % 24;
        }
        return String.format("%02d:%02d", ore, minuti);
    }

    /** Restituisce l'indice di un valore String all'interno di un {@link JsonArray}. */
    private static int jsonArrayIndexOf(final JsonArray array, final String target) {
        for (int i = 0; i < array.size(); i++) if (target.equals(array.get(i).getAsString())) return i;
        return -1;
    }

}
