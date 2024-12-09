package org.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.*;

public class mainGUIcontroller {
    @FXML
    private Label temperatureLabel;

    private StringProperty temperature = new SimpleStringProperty("20°C");

    public StringProperty temperatureProperty() {
        return temperature;
    }

    public void setTemperature(String temp) {
        temperature.set(temp);
    }

    public String getTemperature() {
        return temperature.get();
    }

    @FXML
    public void initialize() {
        // Binding della proprietà con la Label
        temperatureLabel.textProperty().bind(temperature);
    }
    
}
