package org.app.model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;


// https://api.weatherapi.com/v1/current.json?key=4a135623e6644ebeb6b121450232811&q=Rome&aqi=yes
// private final String KEY = "4a135623e6644ebeb6b121450232811";


public class Weather implements WeatherInterface {
    private Map<String, String> locationInfo;
    private AbstractPair<String, String> coords;

    private final String FORECAST_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%LAT&longitude=%LNG&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,wind_speed_10m,wind_direction_10m,pressure_msl,soil_temperature_0cm&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,daylight_duration,sunshine_duration,uv_index_max&timezone=auto&forecast_days=8";
    private final String NOW_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=44.2218&longitude=12.0414&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m&timezone=auto&forecast_days=1";
    private final String URL_CITY_INFO = "https://www.ilmeteo.it/meteo/";

    // 14-days forecast info hour-by-hour
    private final Map<String, Map<String, Map<String, Number>>> FORECAST_HOURS = new HashMap<>();

    // 14-days general forecast info (day weather icon, min/max temperature)
    private final Map<String, Map<String, Number>> DAILY_GENERAL_FORECAST = new HashMap<>();

    // Sunset, Sunrise
    private final Map<String, Map<String, String>> SUN_DAILY_INFO = new HashMap<>();

    // City Info
    private final Map<String, Number> CITY_INFO = new HashMap<>();

    private final Map<String, Number> NOW = new HashMap<>();

    private int forecast_days = 0;

    // Current weather last update vars
    private String lastDataUpdate = "";
    private long last_update = 0;

    private boolean requested = false;

    /* CONSTRUCT */

    public Weather(final Map<String, String> locationInfo) {
        this.setLocation(locationInfo);
    }

    @Override
    public void setLocation(final Map<String, String> locationInfo) {
        this.locationInfo = locationInfo;
        this.coords = new Pair<>(locationInfo.get("lat"), locationInfo.get("lng"));
        this.requested = false;
        this.last_update = 0;
        this.lastDataUpdate = "";
    }

    @Override
    public boolean reqestsAllForecast() {
        try {

            // Get and Parse JSON with wheather info
            final var reader = new AdvancedJsonReader(FORECAST_API_URL
                    .replace("%LAT", this.coords.getX())
                    .replace("%LNG", this.coords.getY()));

            this.CITY_INFO.put("meters_above_sea", reader.getFloat("elevation"));
            final var inhab = this.getCityInhabitants(this.locationInfo.get("city_ascii"));
            if (inhab.isPresent()) {
                this.CITY_INFO.put("inhabitants", inhab.get());
            } else {
                this.CITY_INFO.put("inhabitants", null);
            }

            // Get Weekly Forecast
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
                final JsonElement elem = dates_hours.get(i);
                final String[] dateTime = elem.getAsString().split("T");

                if (!FORECAST_HOURS.containsKey(dateTime[0])) {
                    FORECAST_HOURS.put(dateTime[0], new HashMap<>());
                }

                final Map<String, Number> info = new HashMap<>();
                info.put("temperature_C", temperature_2m.get(i).getAsNumber());
                info.put("temperature_F",
                        Converter.celsiusToFahrenheit(temperature_2m.get(i).getAsDouble()));
                info.put("humidity", humidity_2m.get(i).getAsNumber());
                info.put("apparent_temperature", apparent_temperature.get(i).getAsNumber());
                info.put("precipitation_probability", precipitation_probability.get(i).getAsNumber());
                info.put("precipitation_mm", precipitation.get(i).getAsNumber());
                info.put("precipitation_inch",
                        Converter.mmToInches(precipitation.get(i).getAsDouble()));
                info.put("weather_code", weather_code.get(i).getAsNumber());
                info.put("wind_speed_kmh", wind_speed_10m.get(i).getAsNumber());
                info.put("wind_speed_mph",
                        Converter.kmhToMph(wind_speed_10m.get(i).getAsDouble()));
                info.put("wind_direction", wind_direction_10m.get(i).getAsNumber());
                info.put("pressure", pressure_msl.get(i).getAsNumber());
                try {
                    info.put("soil_temperature", soil_temperature_0cm.get(i).getAsNumber());
                } catch (final Exception e) {  // When soil_temperature is JsonNull
                    info.put("soil_temperature", null);
                }
                final String time = dateTime[1].split(":")[0];
                // Add Instance
                FORECAST_HOURS.get(dateTime[0]).put(time, info);
            }

            // Set forecast days
            this.forecast_days = FORECAST_HOURS.keySet().size();

            // Get Daily General Forecast & Daily Info
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
                final Map<String, Number> forecast_info = new HashMap<>();
                final Map<String, String> other_info = new HashMap<>();
                final String KEY_DAY = days.get(i).getAsString();
                forecast_info.put("weather_code", day_weather_code.get(i).getAsNumber());
                forecast_info.put("temperature_max_C", temperature_2m_max.get(i).getAsNumber());
                forecast_info.put("temperature_max_F",
                        Converter.celsiusToFahrenheit(temperature_2m_max.get(i).getAsDouble()));
                forecast_info.put("temperature_min_C", temperature_2m_min.get(i).getAsNumber());
                forecast_info.put("temperature_min_F",
                        Converter.celsiusToFahrenheit(temperature_2m_min.get(i).getAsDouble()));
                forecast_info.put("daylight_duration", daylight_duration.get(i).getAsNumber());
                forecast_info.put("sunshine_duration", sunshine_duration.get(i).getAsNumber());
                forecast_info.put("uv_max", uv_index_max.get(i).getAsNumber());
                // Add Instance
                DAILY_GENERAL_FORECAST.put(KEY_DAY, forecast_info);

                other_info.put("sunrise", days_sunrise.get(i)
                        .getAsString().split("T")[1]);
                other_info.put("sunset", days_sunset.get(i)
                        .getAsString().split("T")[1]);
                // Add Instance
                SUN_DAILY_INFO.put(KEY_DAY, other_info);
            }

            // NOW
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

    @Override
    public int getForecastDays() {
        return this.forecast_days;
    }

    @Override
    public Optional<Map<String, Map<String, Map<String, Number>>>> getAllForecast() {
        if (!this.requested) {
            return Optional.empty();
        }
        return Optional.of(this.FORECAST_HOURS);
    }

    @Override
    public Optional<Map<String, Map<String, Number>>> getDailyGeneralForecast() {
        if (!this.requested) {
            return Optional.empty();
        }
        return Optional.of(this.DAILY_GENERAL_FORECAST);
    }

    @Override
    public Optional<Map<String, Map<String, String>>> getDailyInfo() {
        if (!this.requested) {
            return Optional.empty();
        }
        return Optional.of(this.SUN_DAILY_INFO);
    }

    @Override
    public Optional<Pair<String, Map<String, Number>>> getWeatherNow() {
        if (this.last_update == 0 ||
            this.checkMinutesPassed(this.last_update, 20)) {
            try {
                final var reader = new AdvancedJsonReader(this.NOW_API_URL);
                if (!this.setCurrentWeather(reader)) {
                    this.last_update = 0;
                    return Optional.empty();
                }
            } catch (final Exception err) {
                this.last_update = 0;
                err.printStackTrace();
                return Optional.empty();
            }
        } else {
            System.out.println("Used cached meteo info");
        }
        return Optional.of(new Pair<>(this.lastDataUpdate, this.NOW));
    }

    @Override
    public Optional<Map<String, Number>> getCityInfo() {
        if (this.last_update == 0) {
            return Optional.empty();
        }
        return Optional.of(this.CITY_INFO);
    }

    @Override
    public String getWindDirection(int degrees) {
        final List<String> DIREZIONI = Arrays.asList(
            "Nord", "Nord-Nordest", "Nordest", "Est-Nordest",
            "Est", "Est-Sudest", "Sudest", "Sud-Sudest",
            "Sud", "Sud-Sudoest", "Sudoest", "Ovest-Sudoest",
            "Ovest", "Ovest-Nord-Ovest", "Nord-Ovest", "Nord-Nord-Ovest"
        );

        degrees = degrees % 360;
        if (degrees < 0) {
            degrees += 360;
        }

        // Calcola l'indice della direzione basato sui gradi
        // Ogni direzione copre 22.5 gradi. Aggiungiamo 11.25 per arrotondare alla direzione più vicina
        double adjustedDegrees = degrees + 11.25;
        if (adjustedDegrees >= 360) {
            adjustedDegrees -= 360;
        }

        final int index = (int)(adjustedDegrees / 22.5) % 16;

        return DIREZIONI.get(index);
    }

    /* PRIVATE FUNCTIONS */

    private boolean setCurrentWeather(final AdvancedJsonReader reader) {
        try {
            this.last_update = System.currentTimeMillis() / 1000L;
            this.lastDataUpdate = reader.getString("current.time");
            this.NOW.put("weather_code",
                    reader.getFromJson("current.weather_code", Number.class));
            this.NOW.put("temperature_C",
                    reader.getFromJson("current.temperature_2m", Number.class));
            this.NOW.put("temperature_F",
                    Converter.celsiusToFahrenheit(reader.getDouble("current.temperature_2m")));
            this.NOW.put("apparent_temperature_C",
                    reader.getFromJson("current.apparent_temperature", Number.class));
            this.NOW.put("apparent_temperature_F",
                    Converter.celsiusToFahrenheit(reader.getDouble("current.apparent_temperature")));
            this.NOW.put("humidity",
                    reader.getFromJson("current.relative_humidity_2m", Number.class));
            this.NOW.put("wind_speed_kmh",
                    reader.getFromJson("current.wind_speed_10m", Number.class));
            this.NOW.put("wind_speed_mph",
                    Converter.kmhToMph(reader.getDouble("current.wind_speed_10m")));
            this.NOW.put("wind_direction",
                    reader.getFromJson("current.wind_direction_10m", Number.class));
            this.NOW.put("precipitation_mm",
                    reader.getFromJson("current.precipitation", Number.class));
            this.NOW.put("precipitation_inch",
                    Converter.mmToInches(reader.getDouble("current.precipitation")));
            this.NOW.put("cloud_cover",
                    reader.getFromJson("current.cloud_cover", Number.class));
            return true;
        } catch (final Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    private boolean checkMinutesPassed(final long timestamp, final int min) {
        final long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        final long minutesInSeconds = (long) (min * 60);
        return (currentTimeInSeconds - timestamp) >= minutesInSeconds;
    }

    private Optional<Integer> getCityInhabitants(final String asciiCityName) {
        try {
            final Document METEO_3B = Jsoup.connect(this.URL_CITY_INFO + asciiCityName).get();
            final Elements raw_general_info = METEO_3B.getElementsByClass("infoloc");

            if (raw_general_info.size() > 0) {
                final Element general_info = raw_general_info.get(0);
                final Pattern pattern = Pattern.compile("([\\d.]+)\\s*abitanti");
                final Matcher matcher = pattern.matcher(general_info.text());

                if (matcher.find()) {
                    // Prendere il numero di abitanti (gruppo 1)
                    final String numeroAbitantiRaw = matcher.group(1);
                    return Optional.of(this.strToInt(numeroAbitantiRaw));
                }
            }
        } catch (final Exception e) {}
        return Optional.empty();
    }

    private int strToInt(final String str) {
        final String numberStr = str.replaceAll("[^\\d]", "");
        return Integer.parseInt(numberStr);
    }

}
