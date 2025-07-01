package org.app.weathermode.view;

import org.app.weathermode.controller.Controller;
import org.app.config.AppConfig;
import org.app.config.ConfigManager;
import org.app.weathermode.model.MoonPhases;
import org.app.weathermode.model.MoonPhasesImpl;

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

/**
 * Finestra modale con le impostazioni dell’app.
 */
public class SettingsWindow extends Stage {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";
    private final Controller controller;

    public SettingsWindow(final Controller controller) {
        this.controller = controller;

        // Costanti locali
        final String windowTitle = "Impostazioni";
        final double rootPadding = 20.0;
        final double vboxSpacing = 15.0;
        final String chartBtnText = "Visualizza Grafico Temperature";
        final String moonBtnText = "Visualizza Luna di Oggi";
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

    @SuppressWarnings({"unchecked","varargs"})
    private void openChart() {
        // Costanti locali
        final String errorTitle = "Dati mancanti";
        final String errorMessage = "Nessun dato di temperatura disponibile.";
        final String dateAxisLabel = "Data";
        final String tempAxisLabel = "Temperatura (°C)";
        final String chartTitle = "Temperature Massime e Minime";
        final String seriesMinName = "Minime";
        final String seriesMaxName = "Massime";
        final double chartWidth = 800.0;
        final double chartHeight = 600.0;

        final Optional<Map<String, Map<String, Number>>> hourlyOpt =
            controller.getWeatherObj().getDailyGeneralForecast();
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

        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(dateAxisLabel);
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(tempAxisLabel);
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(chartTitle);
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);

        final XYChart.Series<String, Number> minSeries = new XYChart.Series<>();
        minSeries.setName(seriesMinName);
        final XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
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
                System.err.println(errorLog);
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
                System.out.println("City ID = " + id);
                controller.forceRefresh();
            });
        });
    }

}
