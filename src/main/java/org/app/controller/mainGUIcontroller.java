package org.app.controller;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.beans.property.*;

public class mainGUIcontroller {
    @FXML
    private Label currentStatusLabel;
    
    @FXML
    private Label currentTemperatureLabel;

    @FXML
    private Label currentPerceivedTemperatureLabel;

    @FXML
    private ImageView currentWeatherImage;

    @FXML
    private Label currentMaxTemperatureLabel;

    @FXML
    private Label currentMinTemperatureLabel;

    @FXML
    private Button settingsButton;

    private StringProperty currentStatusTemperature = new SimpleStringProperty("Soleggiato");
    private StringProperty currentTemperature = new SimpleStringProperty("Temperatura: 20°C");
    private StringProperty currentPerceivedTemperature = new SimpleStringProperty("Percepita: 18°C");
    private StringProperty currentMaxTemperature = new SimpleStringProperty("Max: 15°C");
    private StringProperty currentMinTemperature = new SimpleStringProperty("Min: 5°C");


    public StringProperty temperatureProperty() {
        return currentTemperature;
    }

    public void setTemperature(String temp) {
        currentTemperature.set(temp);
    }

    public String getTemperature() {
        return currentTemperature.get();
    }

    @FXML
    public void initialize() {
        // Binding della proprietà con la Label
        currentStatusLabel.textProperty().bind(currentStatusTemperature);
        currentTemperatureLabel.textProperty().bind(currentTemperature);
        currentPerceivedTemperatureLabel.textProperty().bind(currentPerceivedTemperature);
        currentMaxTemperatureLabel.textProperty().bind(currentMaxTemperature);
        currentMinTemperatureLabel.textProperty().bind(currentMinTemperature);

        Image image = new Image(getClass().getResource("/images/0.png").toExternalForm());
        currentWeatherImage.setImage(image);
        Image settingsButtonImage = new Image("/images/Settings.png");
        ImageView settingsButtonImageView = new ImageView(settingsButtonImage);
        settingsButtonImageView.setFitHeight(100);
        settingsButtonImageView.setFitHeight(100);
        settingsButtonImageView.setPreserveRatio(true);
        settingsButton.setGraphic(settingsButtonImageView);
    }

}
