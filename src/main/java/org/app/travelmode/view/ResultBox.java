package org.app.travelmode.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Window;

/**
 * {@code ResultBox} is a custom JavaFX component that visually presents a weather-based evaluation
 * of a planned route, including a meteorological score, travel details, and a map snapshot.
 * <p>
 * This class extends {@link HBox} and is composed of:
 * <ul>
 *   <li>A numerical "meteo score" label, color-coded based on value</li>
 *   <li>Textual descriptions such as duration, and estimated arrival</li>
 *   <li>A dynamically sized and clipped {@link ImageView} for the map</li>
 * </ul>
 * </p>
 * <p>
 * The layout adapts based on the screen size and window location, ensuring a responsive display.
 * </p>
 *
 * <p>To create a new instance, use the static factory method
 * {@link #create(int, String, String, String, String, Image, Window)},
 * which automatically handles initialization and layout configuration.</p>
 *
 */
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
    private static final double IMAGE_MAX_WIDTH_FACTOR = 0.65;
    private static final int CLIP_ARC_WIDTH = 50;
    private static final int CLIP_ARC_HEIGHT = 50;
    private static final int BORDER_ARC_WIDTH = 55;
    private static final int BORDER_ARC_HEIGHT = 55;
    private static final int SPACING = 50;

    private static final int EXCELLENT_SCORE = 76;
    private static final int GOOD_SCORE = 51;
    private static final int BAD_SCORE = 26;

    private final Label meteoScore;
    private final BorderPane infoPane;
    private final StackPane mapContainer;
    private double maxWidth;

    /**
     * Constructs a {@code ResultBox} instance containing a summary of a travel result.
     * This constructor prepares all components but does not finalize the layout.
     * For correct usage, prefer {@link #create(int, String, String, String, String, Image, Window)}.
     *
     * @param meteoScore  A numerical score from 0 to 100 representing the weather favorability.
     * @param description A textual description of the route.
     * @param duration    The duration of the travel.
     * @param arrivalDate The expected arrival date.
     * @param arrivalTime The expected arrival time.
     * @param mapImage    A static image of the route or map.
     * @param window      The window in which this component is displayed (used for sizing).
     */
    private ResultBox(final int meteoScore, final String description, final String duration, final String arrivalDate,
                      final String arrivalTime, final Image mapImage, final Window window) {
        this.meteoScore = new Label(String.valueOf(meteoScore));
        final Label description1 = new Label(DESCRIPTION_TEXT);
        final Label description2 = new Label(description);
        description2.setWrapText(true);
        final Label durationLbl1 = new Label(DURATION_TEXT);
        final Label durationLbl2 = new Label(duration);
        final Label expectedArrival = new Label(ARRIVAL_TEXT);
        final Label arrivalDate1 = new Label("il " + arrivalDate);
        final Label arrivalTime1 = new Label("alle " + arrivalTime);

        updateMaxWidth(window);

        this.meteoScore.setMinSize(METEO_SCORE_SIZE, METEO_SCORE_SIZE);
        this.meteoScore.setMaxSize(METEO_SCORE_SIZE, METEO_SCORE_SIZE);
        this.meteoScore.getStyleClass().remove("label");
        this.meteoScore.getStyleClass().add("meteo-score");

        final VBox descriptionBox = new VBox(5, description1, description2);
        final VBox durationBox = new VBox(5, durationLbl1, durationLbl2);
        final VBox arrivalBox = new VBox(5, expectedArrival, arrivalDate1, arrivalTime1);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);
        durationBox.setAlignment(Pos.CENTER_LEFT);
        arrivalBox.setAlignment(Pos.CENTER_LEFT);

        this.infoPane = new BorderPane();

        final VBox centerInfoVBox = new VBox(35, descriptionBox, durationBox, arrivalBox);
        centerInfoVBox.setAlignment(Pos.CENTER_LEFT);
        centerInfoVBox.getStyleClass().add("center-info-box");

        infoPane.setTop(this.meteoScore);
        infoPane.setCenter(centerInfoVBox);

        final ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setSmooth(true);
        mapImageView.fitWidthProperty().bind(Bindings.createDoubleBinding(
                () -> this.getWidth() * IMAGE_MAX_WIDTH_FACTOR, this.widthProperty()));
        mapImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
                () -> mapImageView.getFitWidth() / IMAGE_WIDTH_RATIO * IMAGE_HEIGHT_RATIO,
                mapImageView.fitWidthProperty()
        ));

        final Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mapImageView.fitWidthProperty());
        clip.heightProperty().bind(mapImageView.fitHeightProperty());
        clip.setArcWidth(CLIP_ARC_WIDTH);
        clip.setArcHeight(CLIP_ARC_HEIGHT);
        clip.setX(0);
        clip.setY(0);
        mapImageView.setClip(clip);

        final Rectangle border = new Rectangle();
        border.widthProperty().bind(mapImageView.fitWidthProperty().add(10));
        border.heightProperty().bind(mapImageView.fitHeightProperty().add(10));
        border.setArcWidth(BORDER_ARC_WIDTH);
        border.setArcHeight(BORDER_ARC_HEIGHT);
        border.setFill(Color.LIGHTBLUE);

        this.mapContainer = new StackPane(border, mapImageView);

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
        this.setSpacing(SPACING);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("result-box");
        this.addColor(meteoScore);
    }

    /**
     * Completes the layout of the component by attaching child nodes,
     * setting minimum size, and enabling proper horizontal growth behavior.
     * <p>This method must be called exactly once after construction.
     * It is automatically invoked by the {@link #create} factory method.</p>
     */
    private void initialize() {
        this.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        this.getChildren().addAll(infoPane, mapContainer);
        setHgrow(infoPane, Priority.ALWAYS);
    }

    /**
     * Factory method to create and fully initialize a {@code ResultBox}.
     *
     * @param meteoScore  An integer score (0–100) representing weather conditions.
     * @param description A textual description of the route.
     * @param duration    The total estimated duration.
     * @param arrivalDate The date of arrival (formatted as a string).
     * @param arrivalTime The time of arrival (formatted as a string).
     * @param mapImage    A JavaFX {@link Image} displaying the route.
     * @param window      The JavaFX {@link Window} used to calculate responsive size.
     * @return A fully configured {@code ResultBox} ready for display.
     */
    public static ResultBox create(final int meteoScore, final String description, final String duration, final String arrivalDate,
                                   final String arrivalTime, final Image mapImage, final Window window) {
        final ResultBox resultBox = new ResultBox(meteoScore, description, duration, arrivalDate, arrivalTime, mapImage, window);
        resultBox.initialize();
        return resultBox;
    }

    /**
     * Updates the maximum width and height of this {@code ResultBox}
     * based on the screen where the specified window is located.
     *
     * @param window The current window used to calculate responsive layout constraints.
     */
    private void updateMaxWidth(final Window window) {
        final Screen screen = Screen.getScreensForRectangle(window.getX(), window.getY(),
                window.getWidth() * DISPLAY_SCALE_FACTOR, window.getHeight() * DISPLAY_SCALE_FACTOR).get(0);
        this.maxWidth = screen.getBounds().getWidth() * MAX_WIDTH_FACTOR;
        this.setMaxWidth(maxWidth);
        this.setMaxHeight(maxWidth * HEIGHT_RATIO / WIDTH_RATIO);
    }

    /**
     * Applies CSS styling classes to this box and the meteo score label
     * based on the numeric value of the weather score.
     *
     * @param meteoScore An integer score used to determine the color class (0–100).
     */
    private void addColor(final int meteoScore) {
        if (meteoScore >= EXCELLENT_SCORE) {
            this.getStyleClass().add("result-box-green");
            this.meteoScore.getStyleClass().add("meteo-score-green");
        } else if (meteoScore >= GOOD_SCORE) {
            this.getStyleClass().add("result-box-yellow");
            this.meteoScore.getStyleClass().add("meteo-score-yellow");
        } else if (meteoScore >= BAD_SCORE) {
            this.getStyleClass().add("result-box-orange");
            this.meteoScore.getStyleClass().add("meteo-score-orange");
        } else {
            this.getStyleClass().add("result-box-red");
            this.meteoScore.getStyleClass().add("meteo-score-red");
        }
    }
}
