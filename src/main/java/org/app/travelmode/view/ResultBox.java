package org.app.travelmode.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
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

    private final Label meteoScore;
    private final Label description;
    private final Label duration;
    private final Label arrivalTime;
    private final Image mapImage;
    private double maxWidth;

    public ResultBox(final String meteoScore, final String description, final String duration, final String arrivalTime, final Image mapImage, final Window window) {
        this.meteoScore = new Label(meteoScore);
        this.description = new Label(description);
        this.duration = new Label(duration);
        this.arrivalTime = new Label(arrivalTime);
        this.mapImage = mapImage;

        // Dimensioni minime e proporzioni
        double minWidth = 640; // Dimensione minima (16:9)
        double minHeight = 360; // Altezza minima basata sul rapporto 16:9
        // Dimensioni massime come il 60% della larghezza dello schermo
        updateMaxWidth(window);

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
        this.meteoScore.setMaxSize(80, 80);

        // Stile per le etichette descrittive
        Label[] labels = {this.description, this.duration, this.arrivalTime};
        for (Label label : labels) {
            label.setStyle(
                    "-fx-font-size: 20px;" +            // Dimensione testo
                            "-fx-text-fill: #34495e;"           // Colore del testo
            );
        }

        final BorderPane infoPane = new BorderPane();
        infoPane.setTop(this.meteoScore);

        infoPane.setStyle(
                "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        "-fx-border-color: #f3cd09;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );

        infoPane.setPrefWidth(minWidth * 0.3);
        infoPane.setMaxWidth(maxWidth * 0.3);

        final VBox centerInfoVBox = new VBox(30);
        centerInfoVBox.setAlignment(Pos.CENTER_LEFT);
        centerInfoVBox.setStyle(
                "-fx-background-color: #ffffff;" +     // Sfondo bianco
                        "-fx-border-color: #08ebbe;" +         // Colore bordo
                        "-fx-border-width: 1px;" +             // Spessore bordo
                        "-fx-border-radius: 10px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 10px;"         // Arrotondamento sfondo
        );
        centerInfoVBox.getChildren().addAll(this.description, this.duration, this.arrivalTime);
        infoPane.setCenter(centerInfoVBox);


        final ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setSmooth(true);

        // Dimensioni dinamiche dell'immagine
        mapImageView.fitWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(maxWidth, Math.max(minWidth, this.getWidth())) * 0.7,
                this.widthProperty()
        ));
        mapImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
                () -> mapImageView.getFitWidth() / 16 * 9,
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

        // Contenitore per l'immagine con bordo
        final StackPane mapContainer = new StackPane(border, mapImageView);

        mapContainer.setStyle(
                "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        "-fx-border-color: #004a9b;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );


        final HBox mainLayout = new HBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(infoPane, mapContainer);

        mainLayout.setStyle(
                //"-fx-padding: 20px;" +                 // Spaziatura esterna
                "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        "-fx-border-color: #ff0000;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );

        final TitledPane detailsPane = new TitledPane();


        // Aggiungi il layout al componente
        this.setStyle(
                "-fx-padding: 20px;" +                 // Spaziatura esterna
                        "-fx-background-color: #f8f9fa;" +     // Colore di sfondo generale
                        "-fx-border-color: #ced6e0;" +         // Bordo esterno
                        "-fx-border-width: 2px;" +             // Spessore bordo esterno
                        "-fx-border-radius: 15px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 15px;"         // Arrotondamento sfondo
        );

        // Comportamento dinamico delle dimensioni
        this.setMinSize(minWidth, minHeight);
        this.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(this.maxWidth, Math.max(minWidth, this.getWidth())),
                this.widthProperty()
        ));
        this.prefHeightProperty().bind(this.prefWidthProperty().divide(16).multiply(9));
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
        this.maxWidth = screen.getBounds().getWidth() * 0.6;
        this.setMaxWidth(maxWidth);
        this.setMaxHeight(maxWidth * 9 / 16);
    }
}
