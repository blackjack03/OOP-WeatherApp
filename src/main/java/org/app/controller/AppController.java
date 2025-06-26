package org.app.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.app.view.App;
import org.app.model.UnitConversion;
import org.app.model.AllWeather;
import org.app.model.LocationSelector;
import org.app.model.LocationSelectorImpl;
import org.app.model.Pair;
import org.app.model.ConfigManager;
import org.app.model.UserPreferences;
import org.app.view.CustomErrorGUI;

/**
 * Controller responsabile dellʼaggiornamento dinamico della GUI
 * in base ai dati meteo.
 */
public class AppController {

    /* ===================== costanti e formati ===================== */
    private static final int REFRESH_TIME = 20;
    private static final DateTimeFormatter HOUR_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /* ============================ modello ========================= */
    private AllWeather model;
    private Map<String, String> cityInfo;

    /* ============================ vista =========================== */
    private Label lblCity;
    private Label lblCond;
    private Label lblTemp;
    private Label lblFeels;
    private Label lblMin;
    private Label lblMax;
    private ImageView todayIcon;
    private VBox hourlyEntries;
    private HBox forecastStrip;

    /** === ID citta' attuale === */
    private int CITY_ID;
    private LocationSelector selector;
    private boolean city_changed = false;

    /* ============================ timer =========================== */
    private Timeline autoRefresh;

    private App APP;

    /* ========================== costruttore ======================= */
    public AppController() {
        this.APP = new App();
        final var labels = this.APP.getLabels();
        this.lblCity = labels.get("lblCity");
        this.lblCond = labels.get("lblCond");
        this.lblTemp = labels.get("lblTemp");
        this.lblFeels = labels.get("lblFeels");
        this.lblMin = labels.get("lblMin");
        this.lblMax = labels.get("lblMax");
        this.hourlyEntries = this.APP.getHourlyEntries();
        this.forecastStrip = this.APP.getForecastStrip();
        this.todayIcon = this.APP.getTodayIcon();
    }

    public void start() {
        this.selector = (App.getLocationSelector() != null)
                ? App.getLocationSelector()
                : new LocationSelectorImpl();

        this.setCity();

        this.cityInfo = selector.getByID(this.CITY_ID)
                .orElseThrow(() -> new IllegalStateException("ID città non valido"));

        this.model = new AllWeather(this.cityInfo);
        if (!this.model.reqestsAllForecast()) {
            throw new IllegalStateException("Impossibile scaricare i dati meteo iniziali");
        }

        this.refresh();
        this.autoRefresh = new Timeline(new KeyFrame(Duration.minutes(REFRESH_TIME), e -> refresh()));
        this.autoRefresh.setCycleCount(Animation.INDEFINITE);
        this.autoRefresh.play();
    }

    public App getApp() {
        return this.APP;
    }

    /*public AppController(final Label lblCity,
                         final ImageView todayIcon,
                         final Label lblCond,
                         final Label lblTemp,
                         final Label lblFeels,
                         final Label lblMin,
                         final Label lblMax,
                         final VBox hourlyEntries,
                         final HBox forecastStrip) {
        this.selector = (App.getLocationSelector() != null)
                ? App.getLocationSelector()
                : new LocationSelectorImpl();

        this.setCity();

        this.cityInfo = selector.getByID(this.CITY_ID)
                .orElseThrow(() -> new IllegalStateException("ID città non valido"));

        this.model = new AllWeather(this.cityInfo);
        if (!this.model.reqestsAllForecast()) {
            throw new IllegalStateException("Impossibile scaricare i dati meteo iniziali");
        }

        this.lblCity = lblCity;
        this.lblCond = lblCond;
        this.lblTemp = lblTemp;
        this.lblFeels = lblFeels;
        this.lblMin = lblMin;
        this.lblMax = lblMax;
        this.todayIcon = todayIcon;
        this.hourlyEntries = hourlyEntries;
        this.forecastStrip = forecastStrip;

        this.refresh();
        this.autoRefresh = new Timeline(new KeyFrame(Duration.minutes(REFRESH_TIME), e -> refresh()));
        this.autoRefresh.setCycleCount(Animation.INDEFINITE);
        this.autoRefresh.play();
    }*/

    /* ==================== API pubbliche ==================== */

    /** Forza un aggiornamento immediato senza toccare il timer. */
    public void forceRefresh() {
        this.setCity();
        this.refresh();
    }

    /** Ferma il timer; da richiamare quando si chiude la finestra principale. */
    public void stop() {
        if (this.autoRefresh != null) {
            this.autoRefresh.stop();
        }
    }

    /** Prende la citta' dalla configurazione */
    private void setCity() {
        // ConfigManager.loadConfig("src/main/java/org/files/configuration.json");
        final UserPreferences user_config =
                ConfigManager.getConfig().getUserPreferences();
        final Optional<Integer> city = user_config.getDefaultCity();
        if (city.isPresent()) {
            System.out.println("City: " + city.get());
            if (this.CITY_ID != city.get()) {
                this.city_changed = true;
                this.CITY_ID = city.get();
                this.cityInfo = selector.getByID(this.CITY_ID)
                .orElseThrow(() -> new IllegalStateException("ID città non valido"));
                if (this.model != null) {
                    this.model.setLocation(this.cityInfo);
                }
            }
        } else {
            System.out.println("CITTA' NON PRESENTE");
            System.out.println(city);
        }
    }

    /* ================== logica principale refresh ================= */

    private void refresh() {
        final int MAX_ATTEMPTS = 3;
        boolean err_flag = true;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            if (this.model.reqestsAllForecast()) {
                err_flag = false;
                break;
            }
        }
        if (err_flag) {
            System.out.println("ERRORE RICHIESTA DATI METEO");
            CustomErrorGUI.showError(
                    "Impossibile recuperare i dati meteo. Controlla la tua connessione internet e riprova.",
                    "Errore di rete!"
            );
            return;
        }
        final Optional<Pair<String, Map<String, Number>>> nowOpt = model.getWeatherNow(this.city_changed);
        if (this.city_changed) {
            this.city_changed = false;
        }
        final Optional<Map<String, Map<String, Number>>> dailyOpt = model.getDailyGeneralForecast();
        final Optional<Map<String, Map<String, Map<String, Number>>>> hourlyOpt = model.getAllForecast();

        if (nowOpt.isEmpty() || dailyOpt.isEmpty() || hourlyOpt.isEmpty()) {
            System.out.println("EMPTY!");
            return;
        }

        final Map<String, Number> now = nowOpt.get().getY();
        final Map<String, Map<String, Number>> daily = dailyOpt.get();
        final Map<String, Map<String, Map<String, Number>>> hourly = hourlyOpt.get();

        Platform.runLater(() -> {
            updateToday(now);
            updateHourly(hourly);
            updateDaily(daily);
        });
    }

    /* ====================== updater sezioni ====================== */
    private void updateToday(final Map<String, Number> now) {
        lblCity.setText(cityInfo.get("city"));
        lblCond.setText(codeToDescription(now.get("weather_code").intValue()));

        final double tempC = now.get("temperature_C").doubleValue();
        final double tempF = now.get("temperature_F").doubleValue();
        lblTemp.setText(String.format("Temperatura: %.1f °C | %.1f °F", tempC, tempF));

        final double feelC = now.get("apparent_temperature_C").doubleValue();
        final double feelF = now.get("apparent_temperature_F").doubleValue();
        lblFeels.setText(String.format("Percepita: %.1f °C | %.1f °F", feelC, feelF));

        todayIcon.setImage(loadIcon(now.get("weather_code").intValue()));

        /* min-max di oggi */
        final String todayKey = LocalDate.now().toString();
        model.getDailyGeneralForecast().ifPresent(map -> {
            if (map.containsKey(todayKey)) {
                Map<String, Number> today = map.get(todayKey);
                lblMin.setText(String.format("Min: %.0f°C | %.0f°F", today.get("temperature_min_C").doubleValue(), today.get("temperature_min_F").doubleValue()));
                lblMax.setText(String.format("Max: %.0f°C | %.0f°F", today.get("temperature_max_C").doubleValue(), today.get("temperature_max_F").doubleValue()));
            }
        });
    }

    private void updateHourly(final Map<String, Map<String, Map<String, Number>>> hourly) {
        final String todayKey = LocalDate.now().toString();
        if (!hourly.containsKey(todayKey)) return;
        final Map<String, Map<String, Number>> todayMap = hourly.get(todayKey);

        /* prossime 4 ore */
        final LocalTime nowTime = LocalTime.now();
        final List<String> ordered = new ArrayList<>(todayMap.keySet());
        Collections.sort(ordered);
        final List<String> next4 = new ArrayList<>();
        for (final String h : ordered) {
            final LocalTime t = LocalTime.parse(h + ":00:00",
                    DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (t.isAfter(nowTime) && next4.size() < 4) next4.add(h);
        }
        if (next4.isEmpty()) { // fallback domani
            final String tomorrowKey = LocalDate.now().plusDays(1).toString();
            if (hourly.containsKey(tomorrowKey)) {
                next4.addAll(hourly.get(tomorrowKey).keySet().stream().sorted().limit(4).toList());
            }
        }

        hourlyEntries.getChildren().clear();
        for (final String h : next4) {
            hourlyEntries.getChildren().add(createHourlyRow(h, todayMap.get(h)));
        }
    }

    private void updateDaily(final Map<String, Map<String, Number>> daily) {
        forecastStrip.getChildren().clear();
        daily.keySet().stream().sorted().limit(7).forEach(day -> {
            forecastStrip.getChildren().add(createMiniForecast(day, daily.get(day)));
        });
    }

    /* ==================== builders helper ==================== */
    private Node createHourlyRow(String hour, Map<String, Number> info) {
        final HBox row = new HBox(20);
        row.setPadding(new Insets(5));

        final ImageView ico = new ImageView(loadIcon(info.get("weather_code").intValue()));
        ico.setPreserveRatio(true);
        ico.setFitWidth(45);

        final Label lblHour = new Label(hour);
        lblHour.getStyleClass().add("subtitle");
        final Label lblCond = new Label(codeToDescription(info.get("weather_code").intValue()));
        lblCond.getStyleClass().add("subtitle");

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final double tempC = info.get("temperature_C").doubleValue();
        final double tempF = UnitConversion.celsiusToFahrenheit(tempC);
        final double feelC = info.get("apparent_temperature").doubleValue();
        final double feelF = UnitConversion.celsiusToFahrenheit(feelC);

        final VBox temps = new VBox(
                new Label(String.format("Temp: %.1f °C | %.1f °F", tempC, tempF)),
                new Label(String.format("Perc: %.1f °C | %.1f °F", feelC, feelF))
        );

        row.getChildren().addAll(ico, lblHour, lblCond, spacer, temps);
        return row;
    }

    private Node createMiniForecast(final String dayKey, final Map<String, Number> info) {
        final VBox mini = new VBox(5);
        mini.setPadding(new Insets(10));
        mini.setAlignment(Pos.CENTER);
        mini.setPrefWidth(190);
        mini.setStyle("-fx-border-color:black;-fx-border-radius:10;-fx-background-radius:10;-fx-background-color:white;");

        final LocalDate date = LocalDate.parse(dayKey);
        final String txt = date.equals(LocalDate.now()) ? "OGGI" :
                date.equals(LocalDate.now().plusDays(1)) ? "DOMANI" :
                        String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
        final Label lblDay = new Label(txt);
        lblDay.getStyleClass().add("subtitle");

        final ImageView ico = new ImageView(loadIcon(info.get("weather_code").intValue()));
        ico.setPreserveRatio(true);
        ico.setFitWidth(45);

        final String range = String.format(
                "Min: %.0f°C | %.0f°F\nMax: %.0f°C | %.0f°F",
                info.get("temperature_min_C").doubleValue(), info.get("temperature_min_F").doubleValue(),
                info.get("temperature_max_C").doubleValue(), info.get("temperature_max_F").doubleValue());
        final Label lblRange = new Label(range);
        lblRange.setAlignment(Pos.CENTER);

        mini.getChildren().addAll(lblDay, ico, lblRange);
        return mini;
    }

    /* ===================== utility interne ===================== */
    private Image loadIcon(final int code) {
        final String path = "/WMOIcons/%d.png".formatted(code);
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (final Exception e) {
            return new Image(getClass().getResourceAsStream("/logo.png"));
        }
    }

    private String codeToDescription(final int code) {
        return switch (code) {
            case 0 -> "Sereno";
            case 1, 2 -> "Poco nuvoloso";
            case 3 -> "Nuvoloso";
            case 45, 48 -> "Nebbia";
            case 51, 53, 55, 56, 57 -> "Pioviggine";
            case 61, 63, 65 -> "Pioggia";
            case 66, 67 -> "Pioggia gelata";
            case 71, 73, 75 -> "Neve";
            case 80, 81, 82 -> "Rovesci";
            case 95, 96, 99 -> "Temporale";
            default -> "--";
        };
    }

}
