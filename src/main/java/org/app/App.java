package org.app;

import org.app.model.AppConfig;
import org.app.model.ConfigManager;
import org.app.model.LocationSelector;
import org.app.controller.AppController;
import org.app.view.SettingsWindow;

import javafx.application.Application;
import javafx.beans.binding.DoubleExpression;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Weather Dashboard
 *
 *  • Finestra suddivisa in 4 aree logiche, con altezze 70 % / 30 % (come prima)
 *    ma colonne 50 % / 50 % per la fascia superiore e 75 % / 25 % per quella inferiore.
 */
public class App extends Application {

    private static LocationSelector locationSelector;

    public void setLocationSelector(final LocationSelector LS) {
        locationSelector = LS;
    }

    public static LocationSelector getLocationSelector() {
        return locationSelector;
    }

    @Override
    public void start(final Stage primaryStage) {
        final AppConfig appConfig = ConfigManager.getConfig();

        //--------------------------- root (2 righe) ---------------------------
        final GridPane root = new GridPane();
        root.setPadding(new Insets(20));
        root.setHgap(20);
        root.setVgap(20);

        // Righe: 70 % + 30 %
        final RowConstraints topRow = new RowConstraints();
        topRow.setPercentHeight(65);
        final RowConstraints bottomRow = new RowConstraints();
        bottomRow.setPercentHeight(35);
        root.getRowConstraints().addAll(topRow, bottomRow);

        //--------------------------- contenitore TOP (50 / 50) ---------------------------
        final GridPane topGrid = new GridPane();
        topGrid.setHgap(20);
        topGrid.setVgap(20);
        ColumnConstraints topLeft  = new ColumnConstraints();
        topLeft.setPercentWidth(60);
        ColumnConstraints topRight = new ColumnConstraints();
        topRight.setPercentWidth(40);
        topGrid.getColumnConstraints().addAll(topLeft, topRight);

        //--------------------------- contenitore BOTTOM (75 / 25) ---------------------------
        final GridPane bottomGrid = new GridPane();
        bottomGrid.setHgap(20);
        bottomGrid.setVgap(20);
        ColumnConstraints bottomLeft  = new ColumnConstraints();
        bottomLeft.setPercentWidth(80);
        ColumnConstraints bottomRight = new ColumnConstraints();
        bottomRight.setPercentWidth(20);
        bottomGrid.getColumnConstraints().addAll(bottomLeft, bottomRight);

        //---------------- TODAY card ----------------
        final VBox todayBox = createCardVBox();
        todayBox.setSpacing(10);
        final String city = "PLACEHOLDER";
        final Label lblCity = makeTitle(city);
        final ImageView todayIcon = makeIcon("/logo.png", root.widthProperty().multiply(0.10));
        final Label lblOggi  = makeTitle("OGGI");
        final Label lblCond  = makeSubtitle("SOLE");
        final Label lblTemp  = new Label("Temperatura: xx °C");
        final Label lblFeels = new Label("Percepita: xx °C");
        final Label lblMin   = new Label("Min: xx°");
        final Label lblMax   = new Label("Max: xx°");
        todayBox.getChildren().addAll(lblCity, todayIcon, lblOggi, lblCond, lblTemp, lblFeels,
                                       new HBox(20, lblMin, lblMax));

        //---------------- HOURLY panel ----------------
        final VBox hourlyBox = createCardVBox();
        hourlyBox.setSpacing(10);
        final VBox hourlyEntries = new VBox(15);
        final String[] hourLabels = {"15:30", "16:30", "17:30", "18:30"};
        for (String hourLbl : hourLabels) {
            hourlyEntries.getChildren().add(createHourlyRow(root, hourLbl));
        }
        final Label hourlyDetails = new Label("Dettagli aggiuntivi…");
        hourlyDetails.setWrapText(true);
        hourlyDetails.setPrefHeight(100);
        hourlyBox.getChildren().addAll(hourlyEntries, hourlyDetails);
        VBox.setVgrow(hourlyEntries, Priority.ALWAYS);

        //---------------- DAILY forecast strip ----------------
        final HBox forecastStrip = createCardHBox();
        forecastStrip.setSpacing(20);
        forecastStrip.setAlignment(Pos.CENTER_LEFT);
        final ScrollPane forecastScroller = new ScrollPane(forecastStrip);
        forecastScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        forecastScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        forecastScroller.setFitToHeight(true);
        forecastScroller.setPannable(true);
        forecastStrip.getChildren().addAll(
            makeMiniForecast("OGGI",   "/logo.png", root.widthProperty().multiply(0.04)),
            makeMiniForecast("DOMANI", "/logo.png", root.widthProperty().multiply(0.04)),
            makeMiniForecast("xx/xx", "/logo.png",  root.widthProperty().multiply(0.04)),
            makeMiniForecast("xx/xx", "/logo.png",  root.widthProperty().multiply(0.04)),
            makeMiniForecast("xx/xx", "/logo.png",  root.widthProperty().multiply(0.04)),
            makeMiniForecast("xx/xx", "/logo.png",  root.widthProperty().multiply(0.04)),
            makeMiniForecast("xx/xx", "/logo.png",  root.widthProperty().multiply(0.04))
        );
        HBox.setHgrow(forecastStrip, Priority.ALWAYS);

        //---------------- SETTINGS button ----------------
        final Button settingsBtn = new Button();
        final ImageView gearIcon = makeIcon("/gear.png", root.widthProperty().multiply(0.08));
        settingsBtn.setGraphic(gearIcon);
        settingsBtn.setPrefSize(60, 60);
        settingsBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        final AppController controller = new AppController(
        lblCity, todayIcon, lblCond, lblTemp, lblFeels, lblMin, lblMax,
        hourlyEntries, forecastStrip);

        settingsBtn.setOnAction(e -> new SettingsWindow(controller).show());

        //---------------- assemblaggio top & bottom ----------------
        topGrid.add(todayBox,  0, 0);
        topGrid.add(hourlyBox, 1, 0);

        bottomGrid.add(forecastScroller, 0, 0);
        bottomGrid.add(settingsBtn,     1, 0);
        GridPane.setHalignment(settingsBtn, HPos.RIGHT);
        GridPane.setValignment(settingsBtn, VPos.BOTTOM);

        root.add(topGrid,    0, 0);
        root.add(bottomGrid, 0, 1);

        //---------------- scene & stage ----------------
        final Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Weather Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    // ---------------- utility helpers ----------------
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
        return box;
    }

    private HBox createCardHBox() {
        final HBox box = new HBox();
        styleAsCard(box);
        return box;
    }

    private Label makeTitle(String txt) {
        final Label l = new Label(txt);
        l.setFont(Font.font(24));
        return l;
    }

    private Label makeSubtitle(String txt) {
        final Label l = new Label(txt);
        l.setFont(Font.font(18));
        return l;
    }

    private ImageView makeIcon(String resourcePath, javafx.beans.value.ObservableValue<? extends Number> widthBinding) {
        final Image img = new Image(getClass().getResourceAsStream(resourcePath));
        final ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(((DoubleExpression) widthBinding));
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
        return row;
    }

    private VBox makeMiniForecast(String day, String iconPath, javafx.beans.value.ObservableValue<? extends Number> widthBinding) {
        final VBox mini = new VBox(5);
        mini.setAlignment(Pos.CENTER);
        mini.setPadding(new Insets(10));
        mini.setPrefWidth(150);
        mini.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
            new CornerRadii(10), BorderWidths.DEFAULT)));
        mini.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        final Label lblDay   = makeSubtitle(day);
        final ImageView ico  = makeIcon(iconPath, widthBinding);
        final Label lblRange = new Label("Min: xx°C  - Max: xx°C");
        mini.getChildren().addAll(lblDay, ico, lblRange);
        return mini;
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
