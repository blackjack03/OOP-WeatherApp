package org.app;

import org.app.model.AppConfig;
import org.app.model.ConfigManager;
import org.app.model.LocationSelector;
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
 * Weather Dashboard – rifattorizzato per usare vere immagini anziché emoji
 * e mostrare almeno quattro fasce orarie nel pannello "HOURLY".
 */
public class App extends Application {

    private static LocationSelector locationSelector;

    public void setLocationSelector(final LocationSelector LS) {
        locationSelector = LS;
    }

    /** Se hai bisogno di leggerlo da qualunque punto */
    public static LocationSelector getLocationSelector() {
        return locationSelector;
    }

    @Override
    public void start(final Stage primaryStage) {
        final AppConfig appConfig = ConfigManager.getConfig();

        //---------------- root grid (2 × 2, % sizing) ----------------
        final GridPane root = new GridPane();
        root.setPadding(new Insets(20));
        root.setHgap(20);
        root.setVgap(20);

        final ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setPercentWidth(40);
        final ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(60);
        root.getColumnConstraints().addAll(leftCol, rightCol);

        final RowConstraints topRow = new RowConstraints();
        topRow.setPercentHeight(60);
        final RowConstraints bottomRow = new RowConstraints();
        bottomRow.setPercentHeight(40);
        root.getRowConstraints().addAll(topRow, bottomRow);

        //---------------- TODAY card (top‑left) ----------------
        final VBox todayBox = createCardVBox();
        todayBox.setSpacing(10);

        final String city = "PLACEHOLDER";
        final Label lblCity = makeTitle(city);

        // Icona principale della giornata
        final ImageView todayIcon = makeIcon("/logo.png", root.widthProperty().multiply(0.08));

        final Label lblOggi = makeTitle("OGGI");
        final Label lblCond = makeSubtitle("SOLE");
        final Label lblTemp = new Label("Temperatura: xx °C");
        final Label lblFeels = new Label("Percepita: xx °C");
        final Label lblMin = new Label("Min: xx°");
        final Label lblMax = new Label("Max: xx°");

        todayBox.getChildren().addAll(
            lblCity,
            todayIcon,
            lblOggi,
            lblCond,
            lblTemp,
            lblFeels,
            new HBox(20, lblMin, lblMax)
        );

        //---------------- HOURLY panel (top‑right) ----------------
        final VBox hourlyBox = createCardVBox();
        hourlyBox.setSpacing(10);

        // Contenitore per le righe orarie
        final VBox hourlyEntries = new VBox(15);

        // Placeholder per quattro ore: ora corrente + 3 successive
        final String[] hourLabels = {"15:30", "16:30", "17:30", "18:30"};
        for (String hourLbl : hourLabels) {
            hourlyEntries.getChildren().add(createHourlyRow(root, hourLbl));
        }

        // Label informazioni aggiuntive (metà dell'altezza che aveva la TextArea)
        final Label hourlyDetails = new Label("Dettagli aggiuntivi…");
        hourlyDetails.setWrapText(true);
        hourlyDetails.setPrefHeight(100); // ~metà altezza della vecchia TextArea (circa)

        hourlyBox.getChildren().addAll(hourlyEntries, hourlyDetails);
        VBox.setVgrow(hourlyEntries, Priority.ALWAYS);

        //---------------- DAILY forecast strip (bottom, spanning 2 cols) ----------------
        final HBox forecastStrip = createCardHBox();
        forecastStrip.setSpacing(20);
        forecastStrip.setAlignment(Pos.CENTER_LEFT);
        final ScrollPane forecastScroller = new ScrollPane(forecastStrip);
        forecastScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        forecastScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        forecastScroller.setFitToHeight(true);
        forecastScroller.setPannable(true);

        forecastStrip.getChildren().addAll(
            makeMiniForecast("OGGI", "/logo.png", root.widthProperty().multiply(0.03)),
            makeMiniForecast("DOMANI", "/logo.png", root.widthProperty().multiply(0.03)),
            makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.03)),
            makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.03)),
            makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.03)),
            makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.03)),
            makeMiniForecast("xx/xx", "/logo.png", root.widthProperty().multiply(0.03))
        );
        HBox.setHgrow(forecastStrip, Priority.ALWAYS);

        //---------------- SETTINGS button (bottom‑right corner) ----------------
        final Button settingsBtn = new Button();
        final ImageView gearIcon = makeIcon("/logo.png", root.widthProperty().multiply(0.03));
        settingsBtn.setGraphic(gearIcon);
        settingsBtn.setPrefSize(60, 60);
        settingsBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        settingsBtn.setOnAction(e -> new SettingsWindow().show());

        //---------------- add nodes to grid ----------------
        root.add(todayBox, 0, 0);
        root.add(hourlyBox, 1, 0);
        root.add(forecastStrip, 0, 1, 2, 1);
        root.add(settingsBtn, 1, 1);
        GridPane.setHalignment(settingsBtn, HPos.RIGHT);
        GridPane.setValignment(settingsBtn, VPos.BOTTOM);

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

    /**
     * Crea un'ImageView che scala in larghezza mantenendo il rapporto d'aspetto.
     * @param resourcePath percorso (nello classpath) dell'immagine
     * @param widthBinding binding per la larghezza
     */
    private ImageView makeIcon(String resourcePath, javafx.beans.value.ObservableValue<? extends Number> widthBinding) {
        final Image img = new Image(getClass().getResourceAsStream(resourcePath));
        final ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(((DoubleExpression) widthBinding));
        return iv;
    }

    /**
     * Crea una riga del pannello orario.
     */
    private HBox createHourlyRow(GridPane root, String hourText) {
        final HBox row = new HBox(20);
        final ImageView icon = makeIcon("/logo.png", root.widthProperty().multiply(0.05));
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

    /**
     * Piccola card di previsione giornaliera.
     */
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
