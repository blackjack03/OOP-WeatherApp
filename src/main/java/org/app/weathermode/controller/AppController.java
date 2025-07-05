package org.app.weathermode.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
// CHECKSTYLE: AvoidStarImport OFF
import java.util.*;
// CHECKSTYLE: AvoidStarImport ON
import java.util.logging.Logger;

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

import org.app.weathermode.model.AllWeather;
import org.app.config.ConfigManager;
import org.app.weathermode.model.LocationSelector;
import org.app.weathermode.model.LocationSelectorImpl;
import org.app.weathermode.model.Pair;
import org.app.weathermode.model.UnitConversion;
import org.app.config.UserPreferences;
import org.app.weathermode.view.AbstractApp;
import org.app.weathermode.view.ApiKeyForm;
import org.app.weathermode.view.App;
import org.app.weathermode.view.CustomErrorGUI;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>Controller responsabile dellʼaggiornamento dinamico della GUI in base ai dati
 * meteorologici.</p>
 * <p>
 * Il controller incapsula la logica necessaria per
 * <ul>
 *   <li>recuperare la località scelta dall’utente (o una di default),</li>
 *   <li>scaricare i dati meteo correnti, orari e giornalieri tramite {@link AllWeather},</li>
 *   <li>popolare e mantenere aggiornati i vari widget della vista JavaFX,</li>
 *   <li>gestire un <em>timer</em> che esegue un refresh automatico alla frequenza indicata da
 *       {@link #REFRESH_TIME}.</li>
 * </ul>
 * Tutte le operazioni che modificano la UI vengono eseguite sul <em>JavaFX
 * Application Thread</em> tramite {@link Platform#runLater(Runnable)} per garantire
 * la thread‑safety.
 * </p>
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "Intentional exposure of private fields for external interaction"
)
public class AppController implements Controller {

    private static final Logger LOG = Logger.getLogger(ConfigManager.class.getName());

    /**
     * Intervallo tra due refresh automatici (minuti).
     */
    private static final int REFRESH_TIME = 20;
    private static final String API_KEY_ERROR = "Errore nella lettura della chiave API";
    private static final String API_KEY_ERROR_MESSAGE = "La chiave inserita non è valida.\nRitentare l'inserimento.";

    private static final String WEATHER_CODE_KEY = "weather_code";

    /**
     * Wrapper per tutte le previsioni/meteo corrente.
     */
    private AllWeather weatherObj;
    /**
     * Informazioni della città corrente (nome, latitudine, longitudine, …).
     */
    private Map<String, String> cityInfo;

    private final Label lblCity;
    private final Label lblCond;
    private final Label lblTemp;
    private final Label lblFeels;
    private final Label lblMin;
    private final Label lblMax;
    private final Label otherDetails;
    private final Label windInfo;
    private final ImageView todayIcon;
    private final VBox hourlyEntries;
    private final HBox forecastStrip;

    /**
     * Identificativo della città scelta.
     */
    private int cityID;
    private LocationSelector selector;
    private boolean cityChanged;

    private Timeline autoRefresh;

    // CHECKSTYLE: LocalFinalVariableName OFF
    private final AbstractApp mainAPP;
    // CHECKSTYLE: LocalFinalVariableName ON

    /**
     * Crea il controller istanziando la vista {@link App} e memorizzando i
     * riferimenti a tutti i nodi JavaFX che dovranno essere aggiornati.
     * <p>
     * In questa fase non viene ancora eseguito alcun accesso alla rete né
     * scaricato alcun dato meteo: tali operazioni verranno effettuate in
     * {@link #start()}.
     * </p>
     */
    public AppController() {
        this.mainAPP = new App(this);
        final Map<String, Label> labels = this.mainAPP.getLabels();
        this.lblCity = labels.get("lblCity");
        this.lblCond = labels.get("lblCond");
        this.lblTemp = labels.get("lblTemp");
        this.lblFeels = labels.get("lblFeels");
        this.lblMin = labels.get("lblMin");
        this.lblMax = labels.get("lblMax");
        this.otherDetails = labels.get("otherDetails");
        this.windInfo = labels.get("windInfo");
        this.hourlyEntries = this.mainAPP.getHourlyEntries();
        this.forecastStrip = this.mainAPP.getForecastStrip();
        this.todayIcon = this.mainAPP.getTodayIcon();
    }

    /**
     * <p>Inizializza la logica del controller:</p>
     * <ol>
     *   <li>Determina il {@link LocationSelector} da utilizzare (quello
     *       precedentemente impostato dall’utente o un nuovo {@link LocationSelectorImpl}).</li>
     *   <li>Carica l’ID città dalle preferenze via {@link #setCity()}.</li>
     *   <li>Istanzia {@link #weatherObj} con l’oggetto città ottenuto e scarica
     *       tutti i dati meteo correnti/futuri. Se il download fallisce viene
     *       sollevata un’eccezione che interrompe l’avvio.</li>
     *   <li>Esegue un primo {@link #refresh()} per popolare la GUI.</li>
     *   <li>Avvia un {@link Timeline} che richiama periodicamente
     *       {@code refresh()} ogni {@value #REFRESH_TIME} minuti.</li>
     * </ol>
     * Tutte le eccezioni cruciali vengono propagate per fermare l’avvio del
     * programma e notificare l’utente.
     *
     * @throws IllegalStateException se l’ID della città non è valido o se i
     *                               dati meteo iniziali non possono essere
     *                               scaricati.
     */
    @Override
    public void start() {
        this.selector = (App.getLocationSelector() != null)
                ? App.getLocationSelector()
                : new LocationSelectorImpl();

        this.setCity();

        this.cityInfo = selector.getByID(this.cityID)
                .orElseThrow(() -> new IllegalStateException("ID città non valido"));

        this.weatherObj = new AllWeather(this.cityInfo);
        if (!this.weatherObj.reqestsAllForecast()) {
            CustomErrorGUI.showError("Non è stato possibile recuperare i dati meteo."
            + "\nControlla la tua connessione."
            + "\nSe il problema persiste, l'API potrebbe non essere disponibile.",
            "Impossibile recuperare i dati meteo");
            throw new IllegalStateException("Impossibile scaricare i dati meteo iniziali");
        }

        this.refresh();
        this.autoRefresh = new Timeline(new KeyFrame(Duration.minutes(REFRESH_TIME), e -> refresh()));
        this.autoRefresh.setCycleCount(Animation.INDEFINITE);
        this.autoRefresh.play();
    }

    /**
     * @return il riferimento alla vista principale {@link App}.
     */
    @Override
    public AbstractApp getApp() {
        return this.mainAPP;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void requestGoogleApiKey() {
        ApiKeyForm.showAndWait().ifPresent(key -> {
            if (key.isBlank()) {
                showWarning(API_KEY_ERROR, API_KEY_ERROR_MESSAGE);
                requestGoogleApiKey();
            } else {
                ConfigManager.getConfig().getApi().setApiKey(key);
            }
        });
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void showError(final String title, final String message) {
        CustomErrorGUI.showErrorJFX(message, title);
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void showWarning(final String title, final String message) {
        CustomErrorGUI.showWarningJFX(message, title);
    }

    /**
     * @return il wrapper con tutti i dati meteo correnti.
     */
    @Override
    public AllWeather getWeatherObj() {
        return this.weatherObj;
    }

    /* ==================== API pubbliche ==================== */

    /**
     * Forza un <strong>refresh immediato</strong> della GUI e dei dati meteo
     * senza modificare o riavviare il timer automatico.
     * <p>Viene prima verificato se l’utente ha cambiato città nelle
     * preferenze; in tal caso {@link #setCity()} aggiorna la destinazione.</p>
     */
    @Override
    public void forceRefresh() {
        this.setCity();
        this.refresh();
    }

    /**
     * Arresta in modo sicuro il {@link Timeline} di aggiornamento automatico.
     * Deve essere invocato quando la finestra principale viene chiusa per
     * evitare <em>memory leak</em> o richieste di rete superflue.
     */
    @Override
    public void stop() {
        if (this.autoRefresh != null) {
            this.autoRefresh.stop();
        }
    }

    /* ==================== gestione città =================== */

    /**
     * Recupera la città di default dalle preferenze utente e aggiorna i
     * campi {@link #cityID} e {@link #cityInfo} se l’utente ne ha scelta una
     * diversa rispetto alla precedente.
     * <p>
     * In caso di cambio città viene inoltre aggiornato l’oggetto
     * {@link #weatherObj} (se già inizializzato) affinché punti alla nuova
     * località. Il flag {@link #cityChanged} viene impostato per far sì che la
     * successiva chiamata a {@link #refresh()} possa richiedere dati meteo con
     * eventuali parametri (es. “&amp;timezone=auto”) corretti.
     * </p>
     */
    @SuppressFBWarnings(
        value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
        justification = "Field selector is initialized in start() before any call to setCity()"
    )
    private void setCity() {
        final UserPreferences userConfig =
                ConfigManager.getConfig().getUserPreferences();
        final Optional<Integer> city = userConfig.getDefaultCity();
        if (city.isPresent()) {
            LOG.fine("City: " + city.get());
            if (this.cityID != city.get()) {
                this.cityChanged = true;
                this.cityID = city.get();
                this.cityInfo = selector.getByID(this.cityID)
                        .orElseThrow(() -> new IllegalStateException("ID città non valido"));
                if (this.weatherObj != null) {
                    this.weatherObj.setLocation(this.cityInfo);
                }
            }
        } else {
            LOG.fine("CITTA' NON PRESENTE");
            LOG.fine(city.toString());
        }
    }

    /* ================== logica principale refresh ================= */

    /**
     * Raccoglie e compone i dati meteo (corrente, orario, giornaliero)
     * aggiornando tutti i widget della vista.
     * <p>
     * Durante il refresh:
     * <ul>
     *   <li>viene tentato il download dei dati fino a {@code MAX_ATTEMPTS}
     *       volte prima di mostrare un messaggio d’errore;</li>
     *   <li>se la città è stata cambiata si forza un refresh anche lato API
     *       per ottenere i campi calcolati nella corretta timezone;</li>
     *   <li>i metodi {@link #updateToday(Map)}, {@link #updateHourly(Map)} e
     *       {@link #updateDaily(Map)} si occupano di popolare le varie sezioni
     *       della GUI;</li>
     *   <li>le operazioni di manipolazione dei nodi JavaFX sono delegate a
     *       {@link Platform#runLater(Runnable)}.</li>
     * </ul>
     */
    private void refresh() {
        final int maxAttempts = 3;
        boolean errFlag = true;
        for (int i = 0; i < maxAttempts; i++) {
            if (this.weatherObj.reqestsAllForecast()) {
                errFlag = false;
                break;
            }
        }
        if (errFlag) {
            LOG.fine("ERRORE RICHIESTA DATI METEO!");
            CustomErrorGUI.showErrorJFX(
                    "Impossibile recuperare i dati meteo. Controlla la tua connessione internet e riprova.",
                    "Errore di rete!"
            );
            return;
        }
        final Optional<Pair<String, Map<String, Number>>> nowOpt =
                this.weatherObj.getWeatherNow(this.cityChanged);
        if (this.cityChanged) {
            this.cityChanged = false;
        }
        final Optional<Map<String, Map<String, Number>>> dailyOpt =
                this.weatherObj.getDailyGeneralForecast();
        final Optional<Map<String, Map<String, Map<String, Number>>>> hourlyOpt =
                this.weatherObj.getAllForecast();

        if (nowOpt.isEmpty() || dailyOpt.isEmpty() || hourlyOpt.isEmpty()) {
            LOG.fine("Refresh Info EMPTY!");
            return;
        }

        final Map<String, Number> now = nowOpt.get().getY();
        final Map<String, Map<String, Number>> daily = dailyOpt.get();
        final Map<String, Map<String, Map<String, Number>>> hourly = hourlyOpt.get();

        Platform.runLater(() -> {
            updateToday(now);
            updateHourly(hourly);
            updateDaily(daily);
            updateOtherDetails();
        });
    }

    /* ====================== updater sezioni ====================== */

    /**
     * Aggiorna il riquadro “OGGI” con temperatura, condizione atmosferica,
     * percepita, icona meteo e range min/max del giorno corrente.
     *
     * @param now mappa dei valori meteorologici correnti restituita da
     *            {@link AllWeather#getWeatherNow(boolean)}.
     */
    private void updateToday(final Map<String, Number> now) {
        lblCity.setText(cityInfo.get("city"));
        lblCond.setText(codeToDescription(now.get(WEATHER_CODE_KEY).intValue()));

        final double tempC = now.get("temperature_C").doubleValue();
        final double tempF = now.get("temperature_F").doubleValue();
        lblTemp.setText(String.format("Temperatura: %.1f °C | %.1f °F", tempC, tempF));

        final double feelC = now.get("apparent_temperature_C").doubleValue();
        final double feelF = now.get("apparent_temperature_F").doubleValue();
        lblFeels.setText(String.format("Percepita: %.1f °C | %.1f °F", feelC, feelF));

        todayIcon.setImage(loadIcon(now.get(WEATHER_CODE_KEY).intValue()));

        /* min‑max di oggi */
        final String todayKey = LocalDate.now().toString();
        weatherObj.getDailyGeneralForecast().ifPresent(map -> {
            if (map.containsKey(todayKey)) {
                final Map<String, Number> today = map.get(todayKey);
                lblMin.setText(String.format("Min: %.0f°C | %.0f°F",
                    today.get("temperature_min_C").doubleValue(), today.get("temperature_min_F").doubleValue()));
                lblMax.setText(String.format("Max: %.0f°C | %.0f°F",
                    today.get("temperature_max_C").doubleValue(), today.get("temperature_max_F").doubleValue()));
            }
        });

        /* Wind info */
        final double windKmh = now.get("wind_speed_kmh").doubleValue();
        final double windMPH = now.get("wind_speed_mph").doubleValue();
        final String windDirection = weatherObj.getWindDirection(now.get("wind_direction").intValue());
        windInfo.setText(String.format("Wind: %.1f km/h | %.1f mph - Direzione: %s", windKmh, windMPH, windDirection));
    }

    /**
     * Aggiorna il riquadro “Altri dettagli” con informazioni su alba, tramonto,
     * popolazione, altitudine e massimi UV.
     */
    private void updateOtherDetails() {
        final int startCapacity = 65;
        final StringBuilder details = new StringBuilder(startCapacity);
        final Optional<Map<String, Map<String, String>>> dailyInfo =
                this.weatherObj.getDailyInfo();
        if (dailyInfo.isPresent()) {
            final Map.Entry<String, Map<String, String>> entry =
                    dailyInfo.get().entrySet().iterator().next();
            final Map<String, String> sunInfo = entry.getValue();
            final String sunrise = sunInfo.get("sunrise");
            final String sunset = sunInfo.get("sunset");
            if (sunrise != null && sunset != null) {
                details.append("Alba: ").append(sunrise)
                .append(" | Tramonto: ").append(sunset);
            }
        }
        final Optional<Map<String, Number>> cityInfo = this.weatherObj.getCityInfo();
        if (cityInfo.isPresent()) {
            details.append('\n');
            final Number population = cityInfo.get().get("inhabitants");
            if (population != null) {
                details.append("Popolazione: ").append(population)
                .append('\n');
            }
            details.append("Metri sul livello del mare: ")
            .append(cityInfo.get().get("meters_above_sea"));
        }
        final Optional<Map<String, Map<String, Number>>> dailyGeneral =
                this.weatherObj.getDailyGeneralForecast();
        if (dailyGeneral.isPresent()) {
            final Map.Entry<String, Map<String, Number>> entry =
                    dailyGeneral.get().entrySet().iterator().next();
            final Number uvMax = entry.getValue().get("uv_max");
            if (uvMax != null) {
                details.append("\nUV massimo: ").append(uvMax);
            }
        }
        this.otherDetails.setText(details.toString());
    }

    /**
     * Popola la sezione oraria con le <strong>prossime quattro ore</strong> di
     * previsioni a partire dall’ora corrente; se il giorno in corso ha meno di
     * quattro slot rimanenti, recupera i restanti dal giorno successivo.
     *
     * @param hourly mappa di previsioni orarie indicizzate per data (ISO‑8601)
     *               e ora ("HH:mm").
     */
    private void updateHourly(final Map<String, Map<String, Map<String, Number>>> hourly) {
        final String todayKey = LocalDate.now().toString();
        if (!hourly.containsKey(todayKey)) {
            return;
        }
        final Map<String, Map<String, Number>> todayMap = hourly.get(todayKey);

        /* prossime 4 ore */
        final LocalTime nowTime = LocalTime.now();
        final List<String> ordered = new ArrayList<>(todayMap.keySet());
        Collections.sort(ordered);
        final List<String> next = new ArrayList<>();
        final int maxHours = 4;
        for (final String h : ordered) {
            final LocalTime t = LocalTime.parse(h + ":00:00", // NOPMD
                    DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (t.isAfter(nowTime) && next.size() < maxHours) {
                next.add(h);
            }
        }
        final int rest = maxHours - next.size();
        if (next.isEmpty() || rest > 0) { // fallback domani
            final String tomorrowKey = LocalDate.now().plusDays(1).toString();
            if (hourly.containsKey(tomorrowKey)) {
                next.addAll(hourly.get(tomorrowKey).keySet().stream()
                        .sorted().limit(rest).toList());
            }
        }

        hourlyEntries.getChildren().clear();
        for (final String h : next) {
            hourlyEntries.getChildren().add(createHourlyRow(h, todayMap.get(h)));
        }
    }

    /**
     * Aggiorna la <em>strip</em> dei prossimi sette giorni sostituendo i nodi
     * precedenti con nuovi <em>mini‑forecast</em> generati da
     * {@link #createMiniForecast(String, Map)}.
     *
     * @param daily mappa delle previsioni giornaliere restituita da
     *              {@link AllWeather#getDailyGeneralForecast()}.
     */
    private void updateDaily(final Map<String, Map<String, Number>> daily) {
        final int days = 7;
        forecastStrip.getChildren().clear();
        daily.keySet().stream().sorted().limit(days).forEach(day -> {
            forecastStrip.getChildren().add(createMiniForecast(day, daily.get(day)));
        });
    }

    /* ==================== builders helper ==================== */

    /**
     * Costruisce una riga di previsioni orarie (icona, ora, descrizione, temp &amp;
     * percepita) pronta per essere aggiunta al {@link #hourlyEntries}.
     *
     * @param hour ora nel formato "HH:mm".
     * @param info mappa con i dati meteo dell’ora specificata.
     * @return nodo JavaFX rappresentante la riga.
     */
    private Node createHourlyRow(final String hour, final Map<String, Number> info) {
        final int spacing = 20;
        final int topRightBottomLeft = 5;
        final HBox row = new HBox(spacing);
        row.setPadding(new Insets(topRightBottomLeft));

        final ImageView ico = new ImageView(loadIcon(info.get(WEATHER_CODE_KEY).intValue()));
        ico.setPreserveRatio(true);
        final int icoWidth = 45;
        ico.setFitWidth(icoWidth);

        final Label lblHour = new Label(hour);
        lblHour.getStyleClass().add("subtitle");
        final Label lblCond = new Label(codeToDescription(info.get(WEATHER_CODE_KEY).intValue()));
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

    /**
     * Crea un piccolo riquadro riassuntivo (icona + min/max) per il giorno
     * indicato, utilizzato nella <em>forecast strip</em> settimanale.
     *
     * @param dayKey stringa ISO‑8601 della data (es. "2025-06-27").
     * @param info   mappa con temperatura minima/massima e codice meteo.
     * @return nodo JavaFX pronto per essere inserito in {@link #forecastStrip}.
     */
    private Node createMiniForecast(final String dayKey, final Map<String, Number> info) {
        final int spacing = 5;
        final VBox mini = new VBox(spacing);
        final int topRightBottomLeft = 10;
        mini.setPadding(new Insets(topRightBottomLeft));
        mini.setAlignment(Pos.CENTER);
        final int prefWidth = 190;
        mini.setPrefWidth(prefWidth);
        mini.setStyle("-fx-border-color:black;-fx-border-radius:10;-fx-background-radius:10;-fx-background-color:white;");

        final LocalDate date = LocalDate.parse(dayKey);
        final String txt = date.equals(LocalDate.now()) ? "OGGI"
                : date.equals(LocalDate.now().plusDays(1)) ? "DOMANI"
                : String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
        final Label lblDay = new Label(txt);
        lblDay.getStyleClass().add("subtitle");

        final ImageView ico = new ImageView(loadIcon(info.get(WEATHER_CODE_KEY).intValue()));
        ico.setPreserveRatio(true);
        final int icoWidth = 45;
        ico.setFitWidth(icoWidth);

        final String range = String.format(
                "Min: %.0f°C | %.0f°F%nMax: %.0f°C | %.0f°F",
                info.get("temperature_min_C").doubleValue(), info.get("temperature_min_F").doubleValue(),
                info.get("temperature_max_C").doubleValue(), info.get("temperature_max_F").doubleValue());
        final Label lblRange = new Label(range);
        lblRange.setAlignment(Pos.CENTER);

        mini.getChildren().addAll(lblDay, ico, lblRange);
        return mini;
    }

    /* ===================== utility interne ===================== */

    /**
     * Carica l’icona corrispondente al codice WMO; se il file non esiste o non
     * è leggibile, ritorna il logo dell’applicazione come <em>fallback</em>.
     *
     * @param code codice WMO.
     * @return immagine JavaFX.
     */
    private Image loadIcon(final int code) {
        final String path = "/WMOIcons/%d.png".formatted(code);
        try {
            return new Image(AppController.class.getResourceAsStream(path));
        } catch (final Exception e) { // NOPMD suppressed because is a general fallback
            return new Image(AppController.class.getResourceAsStream("/logo.png"));
        }
    }

    /**
     * Converte un codice WMO in descrizione testuale (in italiano).
     *
     * @param code codice WMO.
     * @return descrizione leggibile (es. "Sereno", "Pioggia", …).
     */
    // CHECKSTYLE: MagicNumber OFF
    private String codeToDescription(final int code) {
        return switch (code) {
            case 0 -> "Sereno";
            case 1, 2 -> "Poco nuvoloso";
            case 3 -> "Nuvoloso";
            case 45, 48 -> "Nebbia";
            case 51, 53, 55, 56, 57 -> "Pioviggine";
            case 61, 63, 65 -> "Pioggia";
            case 66, 67 -> "Grandine";
            case 71, 73, 75 -> "Neve";
            case 80, 81, 82 -> "Rovesci";
            case 95, 96, 99 -> "Temporale";
            default -> "--";
        };
    }
    // CHECKSTYLE: MagicNumber ON

}
