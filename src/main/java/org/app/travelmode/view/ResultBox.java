package org.app.travelmode.view;

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


public class ResultBox extends StackPane {

    private final Label meteoScore;
    private final Label description;
    private final Label duration;
    private final Label arrivalTime;
    private final Image mapImage;

    public ResultBox(final String meteoScore, final String description, final String duration, final String arrivalTime, final Image mapImage) {
        this.meteoScore = new Label(meteoScore);
        this.description = new Label(description);
        this.duration = new Label(duration);
        this.arrivalTime = new Label(arrivalTime);
        this.mapImage = mapImage;

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

        final VBox centerInfoVBox = new VBox(30);
        centerInfoVBox.setAlignment(Pos.CENTER_LEFT);
        /*centerInfoVBox.setStyle(
                "-fx-padding: 15px;" +                 // Spaziatura interna
                        "-fx-background-color: #ffffff;" +     // Sfondo bianco
                        "-fx-border-color: #bdc3c7;" +         // Colore bordo
                        "-fx-border-width: 1px;" +             // Spessore bordo
                        "-fx-border-radius: 10px;" +           // Arrotondamento bordo
                        "-fx-background-radius: 10px;"         // Arrotondamento sfondo
        );*/
        centerInfoVBox.getChildren().addAll(this.description, this.duration, this.arrivalTime);
        infoPane.setCenter(centerInfoVBox);


        final ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setFitWidth(1300);
        mapImageView.setFitHeight(740);
        //mapImageView.setPreserveRatio(true);
        mapImageView.setSmooth(true);

        // Creazione di una maschera con bordi arrotondati
        final Rectangle clip = new Rectangle(1300, 740, Color.RED); //TODO: sistemare dimensioni
        clip.setArcWidth(50);  // Arrotondamento orizzontale
        clip.setArcHeight(50); // Arrotondamento verticale
        clip.setX(0);
        clip.setY(0);
        mapImageView.setClip(clip);

        Rectangle border = new Rectangle(1310, 750);
        border.setArcWidth(55);
        border.setArcHeight(55);
        border.setFill(Color.LIGHTBLUE);

        // Contenitore per l'immagine con bordo
        final StackPane mapContainer = new StackPane(border, mapImageView);


        final HBox mainLayout = new HBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(infoPane, mapContainer);

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

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(mainLayout);
    }
}
