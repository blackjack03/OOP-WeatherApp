package org.app.travelmode.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Window;


public class ResultBox extends StackPane {

    private static final String DURATION_TEXT = "Durata:";
    private static final String DESCRIPTION_TEXT = "Tramite:";
    private static final String ARRIVAL_TEXT = "Arrivo previsto";
    private static final int IMAGE_HEIGHT_RATIO = 3;
    private static final int IMAGE_WIDTH_RATIO = 4;
    private static final int HEIGHT_RATIO = 9;
    private static final int WIDTH_RATIO = 16;
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 450;

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

    public ResultBox(final String meteoScore, final String description, final String duration, final String arrivalDate, final String arrivalTime, final Image mapImage, final Window window) {
        this.meteoScore = new Label(meteoScore);
        this.description1 = new Label(DESCRIPTION_TEXT);
        this.description2 = new Label(description);
        this.durationLbl1 = new Label(DURATION_TEXT);
        this.durationLbl2 = new Label(duration);
        this.expectedArrival = new Label(ARRIVAL_TEXT);
        this.arrivalDate = new Label("il " + arrivalDate);
        this.arrivalTime = new Label("alle " + arrivalTime);
        this.mapImage = mapImage;

        updateMaxWidth(window);

        /*
        // Stile per meteoScore
        this.meteoScore.setStyle(
                "-fx-font-size: 24px;" +                // Dimensione testo
                        "-fx-font-weight: bold;" +             // Grassetto
                        "-fx-text-fill: #2c3e50;" +            // Colore del testo
                        "-fx-border-color: #3498db;" +         // Colore del bordo
                        "-fx-border-width: 3px;" +             // Spessore del bordo
                        "-fx-background-color: #ecf0f1;" +     // Colore di sfondo
                        "-fx-alignment: center;" +             // Allineamento del testo
                        "-fx-border-radius: 10px;" +           // Arrotondamento bordi
                        "-fx-background-radius: 10px;"         // Arrotondamento sfondo
        );
        this.meteoScore.setMinSize(80, 80);         // Quadrato 80x80
        this.meteoScore.setMaxSize(80, 80);*/


        // Stile per meteoScore con design più accattivante
        this.meteoScore.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-background-color: linear-gradient(to right, #3498db, #2c3e50);" +
                        "-fx-border-radius: 15px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-padding: 20px;" +
                        "-fx-alignment: center;"
        );
        this.meteoScore.setMinSize(100, 100);
        this.meteoScore.setMaxSize(100, 100);


        // Stile per le etichette descrittive
        Label[] labels = {this.description1, this.description2, this.durationLbl1, this.durationLbl2, this.expectedArrival, this.arrivalDate, this.arrivalTime};
        for (final Label label : labels) {
            label.setStyle(
                    "-fx-font-family: 'Arial Rounded MT Bold', sans-serif;" +
                            "-fx-font-size: 20px;" +
                            "-fx-font-weight: 900;" +
                            "-fx-text-fill: #5b9adf;"
            );
        }

        final VBox descriptionBox = new VBox(5, this.description1, this.description2);
        final VBox durationBox = new VBox(5, this.durationLbl1, this.durationLbl2);
        final VBox arrivalBox = new VBox(5, this.expectedArrival, this.arrivalDate, this.arrivalTime);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);
        durationBox.setAlignment(Pos.CENTER_LEFT);
        arrivalBox.setAlignment(Pos.CENTER_LEFT);

        final VBox centerInfoVBox = new VBox(35, descriptionBox, durationBox, arrivalBox);
        centerInfoVBox.setAlignment(Pos.CENTER_LEFT);
        centerInfoVBox.setStyle(
                "-fx-padding: 20px 0 20px 0;" +
                        //"-fx-background-color: #ffffff;" +     // Sfondo bianco
                        "-fx-border-color: #08ebbe;" +         // Colore bordo
                        "-fx-border-width: 1px;" +             // Spessore bordo
                        "-fx-border-radius: 10px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 10px;"         // Arrotondamento sfondo
        );

        final BorderPane infoPane = new BorderPane();
        infoPane.setTop(this.meteoScore);
        infoPane.setCenter(centerInfoVBox);
        /*infoPane.setStyle(
                "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        // "-fx-border-color: #f3cd09;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );*/

        /*infoPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #f8f9fa, #ecf0f1);" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-padding: 20px;"
        );*/


        //infoPane.setPrefWidth(MIN_WIDTH * 0.35);
        //infoPane.setMaxWidth(maxWidth * 0.35);
        infoPane.setPrefWidth(720 * 0.35);


        final ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setSmooth(true);
        mapImageView.fitWidthProperty().bind(Bindings.createDoubleBinding(() -> this.getWidth() * 0.65, this.widthProperty()));
        mapImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
                () -> mapImageView.getFitWidth() / IMAGE_WIDTH_RATIO * IMAGE_HEIGHT_RATIO,
                mapImageView.fitWidthProperty()
        ));

        // Creazione di una maschera con bordi arrotondati
        final Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mapImageView.fitWidthProperty());
        clip.heightProperty().bind(mapImageView.fitHeightProperty());
        clip.setArcWidth(50);  // Arrotondamento orizzontale
        clip.setArcHeight(50); // Arrotondamento verticale
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
        mapContainer.setStyle(
                "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        // "-fx-border-color: #004a9b;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );


        final HBox mainLayout = new HBox(50, infoPane, mapContainer);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle(
                //"-fx-padding: 20px;" +                 // Spaziatura esterna
                "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        //"-fx-border-color: #ff0000;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );


        this.setStyle(
                "-fx-padding: 20px;" +                 // Spaziatura esterna
                        "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        "-fx-border-color: #ced6e0;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );
        this.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        this.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(this.maxWidth, Math.max(MIN_WIDTH, this.getWidth())),
                this.widthProperty()
        ));
        this.prefHeightProperty().bind(this.prefWidthProperty().divide(WIDTH_RATIO).multiply(HEIGHT_RATIO));
        this.minHeightProperty().bind(mainLayout.heightProperty().add(20 * 2));
        this.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                final Window actualWindow = newScene.getWindow();
                actualWindow.xProperty().addListener((obs, oldVal, newVal) -> updateMaxWidth(actualWindow));
                actualWindow.yProperty().addListener((obs, oldVal, newVal) -> updateMaxWidth(actualWindow));
            }
        });

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(mainLayout);
    }

    private void updateMaxWidth(final Window window) {
        final Screen screen = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth() * 0.8, window.getHeight() * 0.8).get(0);
        this.maxWidth = screen.getBounds().getWidth() * 0.65;
        this.setMaxWidth(maxWidth);
        this.setMaxHeight(maxWidth * HEIGHT_RATIO / WIDTH_RATIO);
    }
}
