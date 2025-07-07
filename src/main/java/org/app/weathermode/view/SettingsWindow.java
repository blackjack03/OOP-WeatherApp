package org.app.weathermode.view;

import org.app.weathermode.controller.Controller;
import org.app.config.AppConfig;
import org.app.config.ConfigManager;
import org.app.weathermode.model.moon.MoonPhases;
import org.app.weathermode.model.moon.MoonPhasesImpl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Finestra modale con le impostazioni dell’app.
 */
@SuppressFBWarnings(
    value = "MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR",
    justification = "Button handlers are set up in constructor but execute"
    + "overridable methods only after construction via events"
)
public class SettingsWindow extends Stage {

    private static final String CONFIG_PATH = "app_config/configuration.json";
    private static final Logger LOG = Logger.getLogger(SettingsWindow.class.getName());
    private final Controller controller;

        /**
     * Costruisce e inizializza la finestra delle impostazioni dell’applicazione.
     * Imposta titolo, modalità modale, dimensioni e layout con pulsanti per:
     * <ul>
     *   <li>visualizzare il grafico delle temperature;</li>
     *   <li>mostrare le fasi lunari di oggi;</li>
     *   <li>cambiare la città selezionata.</li>
     * </ul>
     * Le azioni sui pulsanti si appoggiano al {@link Controller} fornito.
     *
     * @param controller l’istanza di {@link Controller} che coordina le
     *                   operazioni dell’applicazione (recupero dati meteo,
     *                   gestione aggiornamenti, ecc.)
     */
    public SettingsWindow(final Controller controller) {
        this.controller = controller;

        // Costanti locali
        final String windowTitle = "Impostazioni";
        final double rootPadding = 20.0;
        final double vboxSpacing = 15.0;
        final String chartBtnText = "Visualizza Grafico Temperature";

        final String changeCityBtnText = "Cambia Città";
        final String btnFontStyle = "-fx-font-size: 18px;";
        final double minWidth = 400.0;
        final double minHeight = 300.0;
        final double initialWidth = minWidth;
        final double initialHeight = minHeight;

        // Impostazioni finestra
        setTitle(windowTitle);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);

        // Pulsanti
        final Button chartBtn = new Button(chartBtnText);
        chartBtn.setStyle(btnFontStyle);
        chartBtn.setMaxWidth(Double.MAX_VALUE);
        chartBtn.setOnAction(e -> openChart());

        final String moonBtnText = "Visualizza Luna di Oggi";
        final Button moonBtn = new Button(moonBtnText);
        moonBtn.setStyle(btnFontStyle);
        moonBtn.setMaxWidth(Double.MAX_VALUE);
        moonBtn.setOnAction(e -> openMoon());

        final Button changeCityBtn = new Button(changeCityBtnText);
        changeCityBtn.setStyle(btnFontStyle);
        changeCityBtn.setMaxWidth(Double.MAX_VALUE);
        changeCityBtn.setOnAction(e -> openChangeCity());

        // Layout principale
        final VBox root = new VBox(vboxSpacing, chartBtn, moonBtn, changeCityBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(rootPadding));

        final Scene scene = new Scene(root);
        setScene(scene);

        // Dimensioni
        setMinWidth(minWidth);
        setMinHeight(minHeight);
        setWidth(initialWidth);
        setHeight(initialHeight);
        setResizable(false);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    private void openChart() {
        final Optional<Map<String, Map<String, Number>>> hourlyOpt =
            controller.getWeatherObj().getDailyGeneralForecast();
        final String errorTitle = "Dati mancanti";
        final String errorMessage = "Nessun dato di temperatura disponibile.";
        if (hourlyOpt.isEmpty()) {
            CustomErrorGUI.showError(errorMessage, errorTitle);
            return;
        }

        final Map<String, double[]> extremes = new LinkedHashMap<>();
        hourlyOpt.get().forEach((date, todayData) -> {
            final double tempCMin = todayData.get("temperature_min_C").doubleValue();
            final double tempCMax = todayData.get("temperature_max_C").doubleValue();
            extremes.put(date, new double[]{tempCMin, tempCMax});
        });

        final String dateAxisLabel = "Data";
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(dateAxisLabel);
        final NumberAxis yAxis = new NumberAxis();
        final String tempAxisLabel = "Temperatura (°C)";
        yAxis.setLabel(tempAxisLabel);
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        final String chartTitle = "Temperature Massime e Minime";
        lineChart.setTitle(chartTitle);
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);

        final XYChart.Series<String, Number> minSeries = new XYChart.Series<>();
        final String seriesMinName = "Minime";
        minSeries.setName(seriesMinName);
        final XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
        final String seriesMaxName = "Massime";
        maxSeries.setName(seriesMaxName);
        extremes.forEach((date, vals) -> {
            minSeries.getData().add(new XYChart.Data<>(date, vals[0]));
            maxSeries.getData().add(new XYChart.Data<>(date, vals[1]));
        });

        lineChart.getData().addAll(maxSeries, minSeries);

        final Stage chartStage = new Stage();
        chartStage.initOwner(this);
        chartStage.initModality(Modality.WINDOW_MODAL);
        chartStage.setTitle(chartTitle);
        final double chartWidth = 800.0;
        final double chartHeight = 600.0;
        chartStage.setScene(new Scene(lineChart, chartWidth, chartHeight));
        chartStage.show();
    }

    /** Lancia il frame Swing che mostra le fasi lunari di oggi. */
    private void openMoon() {
        // Costanti locali
        final String errorLog = "Errore nel recupero delle informazioni lunari.";
        final String errorTitle = "Errore!";
        final String errorMessage = "Errore nel recupero delle informazioni lunari.";

        final Thread t = new Thread(() -> {
            final MoonPhases moon = new MoonPhasesImpl();
            final Optional<Map<String, String>> moonInfo = moon.getMoonInfo();
            if (moonInfo.isEmpty()) {
                LOG.fine(errorLog);
                CustomErrorGUI.showError(errorMessage, errorTitle);
                return;
            }
            ImageFromURLSwing.viewIMG(
                moon.getImageURL(moonInfo.get().get("image_name")),
                moonInfo.get().get("state"),
                "Today MOON Info"
            );
        }, "MoonInfoSwing");
        t.setDaemon(true);
        t.start();
    }

    /** Lancia il frame JavaFX per cambiare città. */
    private void openChangeCity() {
        Platform.runLater(() -> {
            final LocationSelectorGUI gui = new LocationSelectorGUI();
            final Optional<Integer> res = gui.start(App.getLocationSelector());
            res.ifPresent(id -> {
                final AppConfig appConfig = ConfigManager.getConfig();
                appConfig.getUserPreferences().setDefaultCity(id);
                ConfigManager.saveConfig(CONFIG_PATH);
                LOG.fine("City ID = " + id);
                controller.forceRefresh();
            });
        });
    }

}
