package org.app.weathermode.view;

import javafx.scene.Parent;

import org.app.weathermode.controller.AppController;
import org.app.weathermode.model.LocationSelector;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.DoubleExpression;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Weather Dashboard
 * <p>
 * • Finestra suddivisa in 4 aree logiche, con altezze 70 % / 30 % (come prima)
 *   ma colonne 50 % / 50 % per la fascia superiore e 75 % / 25 % per quella inferiore.
 *   Le card interne ora si adattano fin dal primo layout.
 */
public class App {

    private static LocationSelector locationSelector;

    private final GridPane root;

    public void setLocationSelector(final LocationSelector LS) {
        locationSelector = LS;
    }

    public static LocationSelector getLocationSelector() {
        return locationSelector;
    }

    private final Map<String, Label> labels = new HashMap<>();
    private final ImageView todayIcon;
    private final VBox hourlyEntries;
    private final HBox forecastStrip;

    public Map<String, Label> getLabels() {
        return this.labels;
    }

    public VBox getHourlyEntries() {
        return hourlyEntries;
    }

    public HBox getForecastStrip() {
        return forecastStrip;
    }

    public ImageView getTodayIcon() {
        return todayIcon;
    }

    public App(final AppController appController) {
        // final AppConfig appConfig = ConfigManager.getConfig();

        //--------------------------- root (2 righe) ---------------------------
        this.root = new GridPane();
        root.setPadding(new Insets(20));
        root.setHgap(20);
        root.setVgap(20);
        root.setPrefSize(1000, 600);

        // consenti al root di occupare tutta la finestra fin dal primo pass
        final ColumnConstraints rootColumn = new ColumnConstraints();
        rootColumn.setPercentWidth(100);
        root.getColumnConstraints().add(rootColumn);

        //--------------------------- percentuali righe root ------------------
        final RowConstraints topRow = new RowConstraints();
        topRow.setPercentHeight(65);
        final RowConstraints bottomRow = new RowConstraints();
        bottomRow.setPercentHeight(35);
        root.getRowConstraints().addAll(topRow, bottomRow);

        //--------------------------- contenitore TOP ---------------------------
        final GridPane topGrid = new GridPane();
        topGrid.setHgap(20);
        topGrid.setVgap(20);
        final ColumnConstraints topLeft  = new ColumnConstraints();
        topLeft.setPercentWidth(60);
        final ColumnConstraints topRight = new ColumnConstraints();
        topRight.setPercentWidth(40);
        topGrid.getColumnConstraints().addAll(topLeft, topRight);
        GridPane.setHgrow(topGrid, Priority.ALWAYS);
        GridPane.setVgrow(topGrid, Priority.ALWAYS);

        //--------------------------- contenitore BOTTOM ------------------------
        final GridPane bottomGrid = new GridPane();
        bottomGrid.setHgap(20);
        bottomGrid.setVgap(20);
        final ColumnConstraints bottomLeft  = new ColumnConstraints();
        bottomLeft.setPercentWidth(85);
        final ColumnConstraints bottomRight = new ColumnConstraints();
        bottomRight.setPercentWidth(15);
        bottomGrid.getColumnConstraints().addAll(bottomLeft, bottomRight);
        GridPane.setHgrow(bottomGrid, Priority.ALWAYS);
        GridPane.setVgrow(bottomGrid, Priority.ALWAYS);

        //---------------- TODAY card ----------------------------------------
        final VBox todayBox = createCardVBox();
        todayBox.setSpacing(10);
        final String city = "PLACEHOLDER";
        final Label lblCity = makeTitle(city);
        this.todayIcon = makeIcon("/logo.png", root.widthProperty().multiply(0.10));
        final Label lblOggi = makeTitle("OGGI");
        final Label lblCond = makeSubtitle("SOLE");
        final Label lblTemp = new Label("Temperatura: xx °C");
        final Label lblFeels = new Label("Percepita: xx °C");
        final Label lblMin = new Label("Min: xx°");
        final Label lblMax = new Label("Max: xx°");
        todayBox.getChildren().addAll(lblCity, todayIcon, lblOggi, lblCond, lblTemp, lblFeels,
                new HBox(20, lblMin, lblMax));

        //---------------- HOURLY panel ---------------------------------------
        final VBox hourlyBox = createCardVBox();
        hourlyBox.setSpacing(10);
        hourlyEntries = new VBox(15);
        final String[] hourLabels = {"15:30", "16:30", "17:30", "18:30"};
        for (final String hourLbl : hourLabels) {
            hourlyEntries.getChildren().add(createHourlyRow(root, hourLbl));
        }
        final Label hourlyDetails = new Label("Dettagli aggiuntivi…");
        hourlyDetails.setWrapText(true);
        hourlyDetails.setMaxWidth(Double.MAX_VALUE);
        hourlyDetails.setPrefHeight(100);
        hourlyBox.getChildren().addAll(hourlyEntries, hourlyDetails);
        VBox.setVgrow(hourlyEntries, Priority.ALWAYS);

        //---------------- DAILY forecast strip -------------------------------
        this.forecastStrip = createCardHBox();
        forecastStrip.setSpacing(20);
        forecastStrip.setAlignment(Pos.CENTER_LEFT);
        final ScrollPane forecastScroller = new ScrollPane(forecastStrip);
        forecastScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        forecastScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        forecastScroller.setFitToHeight(true);
        forecastScroller.setPannable(true);
        GridPane.setHgrow(forecastScroller, Priority.ALWAYS);
        GridPane.setVgrow(forecastScroller, Priority.ALWAYS);
        forecastScroller.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        forecastStrip.getChildren().addAll(
                makeMiniForecast("OGGI", "/logo.png", root.widthProperty().multiply(0.04)),
                makeMiniForecast("DOMANI", "/logo.png", root.widthProperty().multiply(0.04)),
                makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.04)),
                makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.04)),
                makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.04)),
                makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.04)),
                makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.04))
        );
        HBox.setHgrow(forecastStrip, Priority.ALWAYS);

        //---------------- SETTINGS button ------------------------------------
        final Button settingsBtn = new Button();
        final ImageView gearIcon = makeIcon("/gear.png", root.widthProperty().multiply(0.06));
        settingsBtn.setGraphic(gearIcon);
        settingsBtn.setPrefSize(48, 48);
        settingsBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        GridPane.setHalignment(settingsBtn, HPos.RIGHT);
        GridPane.setValignment(settingsBtn, VPos.BOTTOM);

        //---------------- label registry -------------------------------------
        this.labels.put("lblCity",  lblCity);
        this.labels.put("lblCond",  lblCond);
        this.labels.put("lblTemp",  lblTemp);
        this.labels.put("lblFeels", lblFeels);
        this.labels.put("lblMin",   lblMin);
        this.labels.put("lblMax",   lblMax);

        settingsBtn.setOnAction(e -> new SettingsWindow(appController).show());

        //---------------- assemblaggio top & bottom --------------------------
        topGrid.add(todayBox,  0, 0);
        topGrid.add(hourlyBox, 1, 0);

        bottomGrid.add(forecastScroller, 0, 0);
        bottomGrid.add(settingsBtn,      1, 0);

        root.add(topGrid,    0, 0);
        root.add(bottomGrid, 0, 1);
    }

    public Parent getRoot() {
        return this.root;
    }

    // ---------------- utility helpers --------------------------------------
    private void styleAsCard(Region region) {
        region.setPadding(new Insets(10));
        region.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(10), BorderWidths.DEFAULT)));
        region.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,
                new CornerRadii(10), Insets.EMPTY)));
    }

    private VBox createCardVBox() {
        final VBox box = new VBox();
        styleAsCard(box);
        GridPane.setHgrow(box, Priority.ALWAYS);
        GridPane.setVgrow(box, Priority.ALWAYS);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return box;
    }

    private HBox createCardHBox() {
        final HBox box = new HBox();
        styleAsCard(box);
        GridPane.setHgrow(box, Priority.ALWAYS);
        GridPane.setVgrow(box, Priority.ALWAYS);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return box;
    }

    private Label makeTitle(String txt) {
        final Label l = new Label(txt);
        l.setFont(Font.font(24));
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Label makeSubtitle(String txt) {
        final Label l = new Label(txt);
        l.setFont(Font.font(18));
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private ImageView makeIcon(String resourcePath, javafx.beans.value.ObservableValue<? extends Number> widthBinding) {
        final Image img = new Image(getClass().getResourceAsStream(resourcePath));
        final ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind((DoubleExpression) widthBinding);
        return iv;
    }

    private HBox createHourlyRow(GridPane root, String hourText) {
        final HBox row = new HBox(20);
        final ImageView icon = makeIcon("/logo.png", root.widthProperty().multiply(0.06));
        final Label lblHour = makeSubtitle(hourText);
        final Label lblCond = makeSubtitle("SOLE");
        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        final VBox tempsBox = new VBox(
                new Label("Temperatura: x °C"),
                new Label("Percepita: x °C")
        );
        row.getChildren().addAll(icon, lblHour, lblCond, spacer, tempsBox);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private VBox makeMiniForecast(String day, String iconPath, javafx.beans.value.ObservableValue<? extends Number> widthBinding) {
        final VBox mini = new VBox(5);
        mini.setAlignment(Pos.CENTER);
        mini.setPadding(new Insets(10));
        mini.setPrefWidth(150);
        styleAsCard(mini);
        final Label lblDay = makeSubtitle(day);
        final ImageView ico = makeIcon(iconPath, widthBinding);
        final Label lblRange = new Label("Min: xx°C  - Max: xx°C");
        mini.getChildren().addAll(lblDay, ico, lblRange);
        mini.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return mini;
    }

}
