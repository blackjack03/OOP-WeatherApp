package org.app.weathermode.view;

import org.app.weathermode.controller.Controller;
import org.app.weathermode.model.AppConfig;
import org.app.weathermode.model.ConfigManager;
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

        setTitle("Impostazioni");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);

        /* ---------- pulsanti ---------- */

        final Button chartBtn = new Button("Visualizza Grafico Temperature");
        chartBtn.setStyle("-fx-font-size: 18px;");
        chartBtn.setMaxWidth(Double.MAX_VALUE);
        chartBtn.setOnAction(e -> this.openChart());

        final Button moonBtn = new Button("Visualizza Luna di Oggi");
        moonBtn.setStyle("-fx-font-size: 18px;");
        moonBtn.setMaxWidth(Double.MAX_VALUE);
        moonBtn.setOnAction(e -> this.openMoon());

        final Button changeCityBtn = new Button("Cambia Città");
        changeCityBtn.setStyle("-fx-font-size: 18px;");
        changeCityBtn.setMaxWidth(Double.MAX_VALUE);
        changeCityBtn.setOnAction(e -> this.openChangeCity());

        /* ---------- layout ---------- */
        final VBox root = new VBox(15, chartBtn, moonBtn, changeCityBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        final Scene scene = new Scene(root);
        setScene(scene);

        // Imposta dimensione minima
        setMinWidth(400);
        setMinHeight(300);

        // Imposta dimensione iniziale = dimensione minima
        setWidth(400);
        setHeight(300);

        setResizable(false);
    }

    /* ---------- handler ---------- */

    @SuppressWarnings({"unchecked","varargs"})
    private void openChart() {
        final Optional<Map<String, Map<String, Number>>> hourlyOpt =
                controller.getWeatherObj().getDailyGeneralForecast();
        if (hourlyOpt.isEmpty()) {
            CustomErrorGUI.showError(
                "Nessun dato di temperatura disponibile.",
                "Dati mancanti"
            );
            return;
        }

        final Map<String, double[]> extremes = new LinkedHashMap<>();

        for (final var date : hourlyOpt.get().keySet()) {
            final Map<String, Number> todayData = hourlyOpt.get().get(date);
            final double tempCMin = todayData.get("temperature_min_C").doubleValue();
            final double tempCMax = todayData.get("temperature_max_C").doubleValue();
            extremes.put(date, new double[]{tempCMin, tempCMax});
        }

        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Data");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperatura (°C)");
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Temperature Massime e Minime");
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);
        final XYChart.Series<String, Number> minSeries = new XYChart.Series<>();
        minSeries.setName("Minime");

        final XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
        maxSeries.setName("Massime");
        extremes.entrySet().stream()
                .forEach(entry -> {
                    final String date = entry.getKey();
                    final double[] vals = entry.getValue();
                    minSeries.getData().add(new XYChart.Data<>(date, vals[0]));
                    maxSeries.getData().add(new XYChart.Data<>(date, vals[1]));
                });

        lineChart.getData().addAll(maxSeries, minSeries);

        final Stage chartStage = new Stage();
        chartStage.initOwner(this);
        chartStage.initModality(Modality.WINDOW_MODAL);
        chartStage.setTitle("Grafico Temperature");
        chartStage.setScene(new Scene(lineChart, 800, 600));
        chartStage.show();
    }

    /** Lancia il frame Swing che mostra le fasi lunari di oggi. */
    private void openMoon() {
        final Thread t = new Thread(() -> {
            final MoonPhases moon = new MoonPhasesImpl();
            final Optional<Map<String, String>> moonInfo = moon.getMoonInfo();
            if (moonInfo.isEmpty()) {
                System.err.println("Errore nel recupero delle informazioni lunari.");
                CustomErrorGUI.showError(
                    "Errore nel recupero delle informazioni lunari.",
                    "Errore!"
                );
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

    /** Lancia il frame Swing per cambiare città. */
    private void openChangeCity() {
        final Thread t = new Thread(() -> {
            final LocationSelectorGUI gui = new LocationSelectorGUI();
            final Optional<Integer> res = gui.start(App.getLocationSelector());
            res.ifPresent(id ->
                Platform.runLater(() -> {
                        final AppConfig appConfig = ConfigManager.getConfig();
                        appConfig.getUserPreferences().setDefaultCity(id);
                        ConfigManager.saveConfig(CONFIG_PATH);
                        System.out.println("City ID = " + id);
                        this.controller.forceRefresh();
                    }
                )
            );
        }, "ChangeCitySwing");
        t.setDaemon(true);
        t.start();
    }

}
