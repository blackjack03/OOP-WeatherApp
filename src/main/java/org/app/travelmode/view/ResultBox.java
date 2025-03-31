package org.app.travelmode.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Window;


public class ResultBox extends HBox {

    private static final String DURATION_TEXT = "Durata:";
    private static final String DESCRIPTION_TEXT = "Tramite:";
    private static final String ARRIVAL_TEXT = "Arrivo previsto";
    private static final int IMAGE_HEIGHT_RATIO = 3;
    private static final int IMAGE_WIDTH_RATIO = 4;
    private static final int HEIGHT_RATIO = 9;
    private static final int WIDTH_RATIO = 16;
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 450;
    private static final double METEO_SCORE_SIZE = 100;
    private static final double DISPLAY_SCALE_FACTOR = 0.8;
    private static final double MAX_WIDTH_FACTOR = 0.65;

    private final Label meteoScore;
    private final Label description1;
    private final Label description2;
    private final Label durationLbl1;
    private final Label durationLbl2;
    private final Label expectedArrival;
    private final Label arrivalDate;
    private final Label arrivalTime;
    private final Image mapImage;
    private double maxWidth;

    public ResultBox(int meteoScore, final String description, final String duration, final String arrivalDate, final String arrivalTime, final Image mapImage, final Window window) {
        this.meteoScore = new Label(String.valueOf(meteoScore));
        this.description1 = new Label(DESCRIPTION_TEXT);
        this.description2 = new Label(description);
        this.description2.setWrapText(true);
        this.durationLbl1 = new Label(DURATION_TEXT);
        this.durationLbl2 = new Label(duration);
        this.expectedArrival = new Label(ARRIVAL_TEXT);
        this.arrivalDate = new Label("il " + arrivalDate);
        this.arrivalTime = new Label("alle " + arrivalTime);
        this.mapImage = mapImage;

        updateMaxWidth(window);

        this.meteoScore.setMinSize(METEO_SCORE_SIZE, METEO_SCORE_SIZE);
        this.meteoScore.setMaxSize(METEO_SCORE_SIZE, METEO_SCORE_SIZE);
        this.meteoScore.getStyleClass().remove("label");
        this.meteoScore.getStyleClass().add("meteo-score");

        final VBox descriptionBox = new VBox(5, this.description1, this.description2);
        final VBox durationBox = new VBox(5, this.durationLbl1, this.durationLbl2);
        final VBox arrivalBox = new VBox(5, this.expectedArrival, this.arrivalDate, this.arrivalTime);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);
        durationBox.setAlignment(Pos.CENTER_LEFT);
        arrivalBox.setAlignment(Pos.CENTER_LEFT);

        final BorderPane infoPane = new BorderPane();

        final VBox centerInfoVBox = new VBox(35, descriptionBox, durationBox, arrivalBox);
        centerInfoVBox.setAlignment(Pos.CENTER_LEFT);
        centerInfoVBox.getStyleClass().add("center-info-box");

        infoPane.setTop(this.meteoScore);
        infoPane.setCenter(centerInfoVBox);

        final ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setSmooth(true);
        mapImageView.fitWidthProperty().bind(Bindings.createDoubleBinding(() -> this.getWidth() * 0.65, this.widthProperty()));
        mapImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
                () -> mapImageView.getFitWidth() / IMAGE_WIDTH_RATIO * IMAGE_HEIGHT_RATIO,
                mapImageView.fitWidthProperty()
        ));

        final Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mapImageView.fitWidthProperty());
        clip.heightProperty().bind(mapImageView.fitHeightProperty());
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        clip.setX(0);
        clip.setY(0);
        mapImageView.setClip(clip);

        final Rectangle border = new Rectangle();
        border.widthProperty().bind(mapImageView.fitWidthProperty().add(10));
        border.heightProperty().bind(mapImageView.fitHeightProperty().add(10));
        border.setArcWidth(55);
        border.setArcHeight(55);
        border.setFill(Color.LIGHTBLUE);

        final StackPane mapContainer = new StackPane(border, mapImageView);

        this.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(this.maxWidth, Math.max(MIN_WIDTH, this.getWidth())),
                this.widthProperty()
        ));
        this.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                final Window actualWindow = newScene.getWindow();
                actualWindow.xProperty().addListener((obs, oldVal, newVal) -> updateMaxWidth(actualWindow));
                actualWindow.yProperty().addListener((obs, oldVal, newVal) -> updateMaxWidth(actualWindow));
            }
        });
        this.setSpacing(50);
        this.setAlignment(Pos.CENTER);
        this.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        this.getStyleClass().add("result-box");
        this.addColor(meteoScore);
        this.getChildren().addAll(infoPane, mapContainer);
        HBox.setHgrow(infoPane, Priority.ALWAYS);
    }

    private void updateMaxWidth(final Window window) {
        final Screen screen = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth() * DISPLAY_SCALE_FACTOR, window.getHeight() * DISPLAY_SCALE_FACTOR).get(0);
        this.maxWidth = screen.getBounds().getWidth() * MAX_WIDTH_FACTOR;
        this.setMaxWidth(maxWidth);
        this.setMaxHeight(maxWidth * HEIGHT_RATIO / WIDTH_RATIO);
    }

    private void addColor(int meteoScore) {
        if (meteoScore >= 76) {
            this.getStyleClass().add("result-box-green");
            this.meteoScore.getStyleClass().add("meteo-score-green");
        } else if (meteoScore >= 51) {
            this.getStyleClass().add("result-box-yellow");
            this.meteoScore.getStyleClass().add("meteo-score-yellow");
        } else if (meteoScore >= 26) {
            this.getStyleClass().add("result-box-orange");
            this.meteoScore.getStyleClass().add("meteo-score-orange");
        } else {
            this.getStyleClass().add("result-box-red");
            this.meteoScore.getStyleClass().add("meteo-score-red");
        }
    }
}
