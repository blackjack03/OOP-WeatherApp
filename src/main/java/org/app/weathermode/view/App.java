package org.app.weathermode.view;

// CHECKSTYLE: AvoidStarImport OFF
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
// CHECKSTYLE: AvoidStarImport ON
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.value.ObservableValue;

import org.app.weathermode.controller.Controller;
import org.app.weathermode.model.LocationSelector;

import java.util.HashMap;
import java.util.Map;

/**
 * Weather Dashboard
 * <p>
 * Finestra suddivisa in 4 aree logiche,
 * con righe 65% / 35% e colonne 60% / 40% (top),
 * 85% / 15% (bottom).
 */
public class App implements AbstractApp {

    private static LocationSelector locationSelector;
    private final GridPane root;
    private final Map<String, Label> labels = new HashMap<>();
    private final ImageView todayIcon;
    private final VBox hourlyEntries;
    private final HBox forecastStrip;

    /**
     * Costruisce l’intera scena JavaFX della dashboard meteo e la collega al
     * controller fornito.
     *
     * @param appController il controller responsabile di popolare e aggiornare la vista
     */
    public App(final Controller appController) {
        // Costanti locali per layout
        final double rootPadding = 20.0;
        final double rootGap = 20.0;
        final double prefWidth = 1000.0;
        final double prefHeight = 600.0;
        final double topRowPercent = 65.0;
        final double bottomRowPercent = 35.0;
        final double topLeftPercent = 60.0;
        final double topRightPercent = 40.0;
        final double bottomLeftPercent = 85.0;
        final double bottomRightPercent = 15.0;
        final double todaySpacing = 10.0;
        final double hourlySpacing = 10.0;
        final double hourlyEntrySpacing = 15.0;
        final double hboxSpacing = 20.0;
        final double miniSpacing = 5.0;
        final double miniPrefWidth = 150.0;
        final double iconTodayRatio = 0.10;
        final double iconHourlyRatio = 0.06;
        final double iconForecastRatio = 0.04;
        final double iconSettingsRatio = 0.06;
        final double settingsBtnSize = 48.0;
        final String defaultCity = "PLACEHOLDER";
        final String defaultCondition = "SOLE";
        final String[] defaultHours = {"15:30", "16:30", "17:30", "18:30"};

        // root setup
        root = new GridPane();
        root.setPadding(new Insets(rootPadding));
        root.setHgap(rootGap);
        root.setVgap(rootGap);
        root.setPrefSize(prefWidth, prefHeight);

        final ColumnConstraints fullCol = new ColumnConstraints();
        fullCol.setPercentWidth(100.0);
        root.getColumnConstraints().add(fullCol);

        final RowConstraints rowTop = new RowConstraints();
        rowTop.setPercentHeight(topRowPercent);
        final RowConstraints rowBottom = new RowConstraints();
        rowBottom.setPercentHeight(bottomRowPercent);
        root.getRowConstraints().addAll(rowTop, rowBottom);

        // top grid
        final GridPane topGrid = new GridPane();
        topGrid.setHgap(rootGap);
        topGrid.setVgap(rootGap);
        final ColumnConstraints colLeft = new ColumnConstraints();
        colLeft.setPercentWidth(topLeftPercent);
        final ColumnConstraints colRight = new ColumnConstraints();
        colRight.setPercentWidth(topRightPercent);
        topGrid.getColumnConstraints().addAll(colLeft, colRight);
        GridPane.setHgrow(topGrid, Priority.ALWAYS);
        GridPane.setVgrow(topGrid, Priority.ALWAYS);

        // bottom grid
        final GridPane bottomGrid = new GridPane();
        bottomGrid.setHgap(rootGap);
        bottomGrid.setVgap(rootGap);
        final ColumnConstraints colBotLeft = new ColumnConstraints();
        colBotLeft.setPercentWidth(bottomLeftPercent);
        final ColumnConstraints colBotRight = new ColumnConstraints();
        colBotRight.setPercentWidth(bottomRightPercent);
        bottomGrid.getColumnConstraints().addAll(colBotLeft, colBotRight);
        GridPane.setHgrow(bottomGrid, Priority.ALWAYS);
        GridPane.setVgrow(bottomGrid, Priority.ALWAYS);

        // TODAY card
        final VBox todayBox = createCardVBox();
        todayBox.setSpacing(todaySpacing);
        final Label lblCity = makeTitle(defaultCity);
        todayIcon = makeIcon("/logo.png", root.widthProperty().multiply(iconTodayRatio));
        final Label lblOggi = makeTitle("OGGI");
        final Label lblCond = makeSubtitle(defaultCondition);
        final Label lblTemp = new Label("Temperatura: xx °C");
        final Label lblFeels = new Label("Percepita: xx °C");
        final Label lblMin = new Label("Min: xx°");
        final Label lblMax = new Label("Max: xx°");
        final Label windInfo = new Label("Wind: ...");
        todayBox.getChildren().addAll(lblCity, todayIcon, lblOggi, lblCond, lblTemp, lblFeels,
                new HBox(hboxSpacing, lblMin, lblMax));
        todayBox.getChildren().add(windInfo);

        // HOURLY panel
        final VBox hourlyBox = createCardVBox();
        hourlyBox.setSpacing(hourlySpacing);
        hourlyEntries = new VBox(hourlyEntrySpacing);
        for (final String hour : defaultHours) {
            hourlyEntries.getChildren().add(createHourlyRow(root, hour, defaultCondition, iconHourlyRatio, hboxSpacing));
        }
        final Label otherDetails = new Label("Dettagli aggiuntivi…");
        otherDetails.setWrapText(true);
        otherDetails.setMaxWidth(Double.MAX_VALUE);
        otherDetails.setPrefHeight(100);
        hourlyBox.getChildren().addAll(hourlyEntries, otherDetails);
        VBox.setVgrow(hourlyEntries, Priority.ALWAYS);

        // DAILY forecast strip
        forecastStrip = createCardHBox();
        forecastStrip.setSpacing(hboxSpacing);
        forecastStrip.setAlignment(Pos.CENTER_LEFT);
        final ScrollPane forecastScroller = new ScrollPane(forecastStrip);
        forecastScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        forecastScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        forecastScroller.setFitToHeight(true);
        forecastScroller.setPannable(true);
        GridPane.setHgrow(forecastScroller, Priority.ALWAYS);
        GridPane.setVgrow(forecastScroller, Priority.ALWAYS);
        forecastScroller.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        final String defaultPlaceHolder = "xx/xx";
        for (final String day : new String[]{"OGGI", "DOMANI", defaultPlaceHolder,
            defaultPlaceHolder,
            defaultPlaceHolder,
            defaultPlaceHolder,
            defaultPlaceHolder}) {
            forecastStrip.getChildren().add(
                    makeMiniForecast(day, "/logo.png",
                    root.widthProperty().multiply(iconForecastRatio), miniSpacing, miniPrefWidth));
        }
        HBox.setHgrow(forecastStrip, Priority.ALWAYS);

        // SETTINGS button
        final Button settingsBtn = new Button();
        final ImageView gearIcon = makeIcon("/gear.png", root.widthProperty().multiply(iconSettingsRatio));
        settingsBtn.setGraphic(gearIcon);
        settingsBtn.setPrefSize(settingsBtnSize, settingsBtnSize);
        settingsBtn.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        GridPane.setHalignment(settingsBtn, HPos.RIGHT);
        GridPane.setValignment(settingsBtn, VPos.BOTTOM);

        // registrazione labels
        labels.put("lblCity", lblCity);
        labels.put("lblCond", lblCond);
        labels.put("lblTemp", lblTemp);
        labels.put("lblFeels", lblFeels);
        labels.put("lblMin", lblMin);
        labels.put("lblMax", lblMax);
        labels.put("otherDetails", otherDetails);
        labels.put("windInfo", windInfo);

        settingsBtn.setOnAction(e -> new SettingsWindow(appController).show());

        // assemblo
        topGrid.add(todayBox,  0, 0);
        topGrid.add(hourlyBox, 1, 0);
        bottomGrid.add(forecastScroller, 0, 0);
        bottomGrid.add(settingsBtn,      1, 0);
        root.add(topGrid,    0, 0);
        root.add(bottomGrid, 0, 1);
    }

    /**
     * Imposta il {@link LocationSelector} usato dall’applicazione.
     *
     * @param ls il selettore di località da registrare
     */
    @Override
    public void setLocationSelector(final LocationSelector ls) {
        locationSelector = ls;
    }

    /**
     * Restituisce il {@link LocationSelector} attualmente registrato
     * (può essere {@code null} se non è stato ancora impostato).
     *
     * @return il selettore di località corrente
     */
    public static LocationSelector getLocationSelector() {
        return locationSelector;
    }

    /**
     * Espone la mappa di etichette (key → {@link Label}) che il controller
     * andrà a popolare o aggiornare.
     *
     * @return la mappa delle label
     */
    @Override
    public Map<String, Label> getLabels() {
        return labels;
    }

    /**
     * Restituisce il contenitore verticale che ospita le righe dell’andamento
     * orario, permettendo al controller di aggiungere o sostituire voci.
     *
     * @return il {@link VBox} per le previsioni orarie
     */
    @Override
    public VBox getHourlyEntries() {
        return hourlyEntries;
    }

    /**
     * Restituisce la strip orizzontale con la previsione su più giorni, così che
     * il controller possa popolarla o aggiornarla.
     *
     * @return l’{@link HBox} con la previsione giornaliera
     */
    @Override
    public HBox getForecastStrip() {
        return forecastStrip;
    }

    /**
     * Restituisce l’icona che rappresenta il meteo odierno, in modo che
     * il controller possa sostituirne l’immagine.
     *
     * @return l’{@link ImageView} dell’icona odierna
     */
    @Override
    public ImageView getTodayIcon() {
        return todayIcon;
    }

    /**
     * Restituisce il nodo radice della scena, pronto per essere
     * impostato su uno {@link javafx.stage.Stage} o incorporato altrove.
     *
     * @return il nodo radice della vista
     */
    @Override
    public Parent getRoot() {
        return root;
    }

    private void styleAsCard(final Region region) {
        final double padding = 10.0;
        final double corner = 10.0;
        region.setPadding(new Insets(padding));
        region.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(corner), BorderWidths.DEFAULT)));
        region.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,
                new CornerRadii(corner), Insets.EMPTY)));
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

    private Label makeTitle(final String txt) {
        final double size = 24.0;
        final Label l = new Label(txt);
        l.setFont(Font.font(size));
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Label makeSubtitle(final String txt) {
        final double size = 18.0;
        final Label l = new Label(txt);
        l.setFont(Font.font(size));
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private ImageView makeIcon(final String path, final ObservableValue<? extends Number> widthBinding) {
        final Image img = new Image(App.class.getResourceAsStream(path));
        final ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind((DoubleExpression) widthBinding);
        return iv;
    }

    private HBox createHourlyRow(final GridPane rootPane,
                                 final String hourText,
                                 final String condition,
                                 final double iconRatio,
                                 final double spacing) {
        final HBox row = new HBox(spacing);
        final ImageView icon = makeIcon("/logo.png", rootPane.widthProperty().multiply(iconRatio));
        final Label lblHour = makeSubtitle(hourText);
        final Label lblCond = makeSubtitle(condition);
        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        final VBox temps = new VBox(
                new Label("Temperatura: x °C"),
                new Label("Percepita: x °C")
        );
        row.getChildren().addAll(icon, lblHour, lblCond, spacer, temps);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private VBox makeMiniForecast(final String day,
                                  final String iconPath,
                                  final ObservableValue<? extends Number> widthBinding,
                                  final double spacing,
                                  final double prefW) {
        final VBox mini = new VBox(spacing);
        mini.setAlignment(Pos.CENTER);
        mini.setPadding(new Insets(10.0));
        mini.setPrefWidth(prefW);
        styleAsCard(mini);
        final Label lblDay = makeSubtitle(day);
        final ImageView ico = makeIcon(iconPath, widthBinding);
        final Label lblRange = new Label("Min: xx°C  - Max: xx°C");
        mini.getChildren().addAll(lblDay, ico, lblRange);
        mini.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return mini;
    }

}
