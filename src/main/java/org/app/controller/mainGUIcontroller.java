package org.app.controller;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;

import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.app.model.LocationSelector;
import org.app.model.Pair;
import org.app.model.Weather;

import javafx.beans.property.*;

public class mainGUIcontroller {
    @FXML
    private TextField citySearchField;

    @FXML
    private ListView<String> cityListView;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label windSpeedLabel;

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

    private LocationSelector locationSelector;
    private Weather weather;
    private List<Pair<String, Integer>> possibleCities = new ArrayList<>();

    /*
    private StringProperty currentStatusTemperature = new SimpleStringProperty("Soleggiato");
    private StringProperty currentTemperature = new SimpleStringProperty("Temperatura: 20°C");
    private StringProperty currentPerceivedTemperature = new SimpleStringProperty("Percepita: 18°C");
    private StringProperty currentMaxTemperature = new SimpleStringProperty("Max: 15°C");
    private StringProperty currentMinTemperature = new SimpleStringProperty("Min: 5°C");
    */
    
    /* 
    public StringProperty temperatureProperty() {
        return currentTemperature;
    }
    
    public void setTemperature(String temp) {
        currentTemperature.set(temp);
    }
    
    public String getTemperature() {
        return currentTemperature.get();
    }
    
    public void WeatherController() {
        this.locationSelector = new LocationSelector();
    }
    */
    
    @FXML
    public void initialize() {
        /*
        // Binding della proprietà con la Label
        currentStatusLabel.textProperty().bind(currentStatusTemperature);
        currentTemperatureLabel.textProperty().bind(currentTemperature);
        currentPerceivedTemperatureLabel.textProperty().bind(currentPerceivedTemperature);
        currentMaxTemperatureLabel.textProperty().bind(currentMaxTemperature);
        currentMinTemperatureLabel.textProperty().bind(currentMinTemperature);
        
        // Testing per immagini
        Image image = new Image(getClass().getResource("/images/0.png").toExternalForm());
        currentWeatherImage.setImage(image);
        
        // Configurata l'immagine per le impostazioni
        Image settingsButtonImage = new Image("/images/Settings.png");
        ImageView settingsButtonImageView = new ImageView(settingsButtonImage);
        settingsButtonImageView.setFitHeight(100);
        settingsButtonImageView.setFitHeight(100);
        settingsButtonImageView.setPreserveRatio(true);
        settingsButton.setGraphic(settingsButtonImageView);
        */
        this.weather = new Weather(Map.of("lat", "44.2333", "lng", "12.0500"));
    
        this.locationSelector = new LocationSelector();

        cityListView.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->
            {
                if (newValue != null && possibleCities != null &&!newValue.equals(oldValue)) {
                    //updateWeatherInfo(newValue.getY());
                    Optional<Pair<String, Integer>> city = possibleCities.stream()
                            .filter(c -> c.getX().equals(newValue))
                            .findFirst();
                    city.ifPresent(pair -> updateWeatherInfo(pair.getY()));
                }
            });
            
        citySearchField.addEventFilter(KeyEvent.KEY_RELEASED, this::onCitySearch);

    }

    private void onCitySearch(KeyEvent event) {
        String query = citySearchField.getText().toLowerCase();
        //String searchText = citySearchField.getText();

        //List<Pair<String, Integer>> possibleCities = locationSelector.getPossibleLocations(query);
        possibleCities = locationSelector.getPossibleLocations(query);

        List<String> cityNames = new ArrayList<>();
        for (Pair<String, Integer> city : possibleCities) {
            cityNames.add(city.getX());
        }

        cityListView.setItems(FXCollections.observableArrayList(cityNames));

        if (!possibleCities.isEmpty()) {
            updateWeatherInfo(possibleCities.get(0).getY());
        } else {
            cityListView.setItems(FXCollections.observableArrayList("Nessuna città trovata"));
        }
    }

    private void updateWeatherInfo(int cityID) {
        Optional<Map<String, String>> locationInfoOpt = locationSelector.getByID(cityID);
        if (locationInfoOpt.isPresent()) {
            Map<String, String> locationInfo = locationInfoOpt.get();

            weather.setLocation(locationInfo);
            if (weather.reqestsAllForecast()) {
                updateLabels();
            }
        }
    }

    private void updateLabels() {
        Optional<Pair<String, Map<String, Number>>> currentWeatherOpt = weather.getWeatherNow();

        if (currentWeatherOpt.isPresent()) {
            Pair<String, Map<String, Number>> currentWeatherPair = currentWeatherOpt.get();
            Map<String, Number> currentWeather = currentWeatherPair.getY();
            currentTemperatureLabel.setText("Temperatura: " + currentWeather.get("temperature_C") + " °C");
            humidityLabel.setText("Umidità: " + currentWeather.get("humidity") + "%");
            windSpeedLabel.setText("Velocità del vento: " + currentWeather.get("wind_speed_kmh") + " km/h");
        }
    }

}
