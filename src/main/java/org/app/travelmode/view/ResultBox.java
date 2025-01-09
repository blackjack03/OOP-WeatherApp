package org.app.travelmode.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

        final VBox infoVBox = new VBox(10);
        infoVBox.getChildren().addAll(this.meteoScore, this.description, this.duration, this.arrivalTime);

        final HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        final ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setFitWidth(1000);
        mapImageView.setFitHeight(800);
        mapImageView.setPreserveRatio(true);
        mainLayout.getChildren().addAll(infoVBox, mapImageView);

        final TitledPane detailsPane = new TitledPane();

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(mainLayout);
    }
}
