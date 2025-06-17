package org.app.controller;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;

// import javafx.collections.FXCollections;

import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.app.model.LocationSelector;
import org.app.model.LocationSelectorImpl;
import org.app.model.Pair;
import org.app.model.AllWeather;

// import javafx.beans.property.*;

public class mainGUIcontroller {
    @FXML
    private TextField citySearchField;

    @FXML
    private ListView<String> cityListView;

    @FXML
    private ContextMenu cityContextMenu;

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

    private final LocationSelector locationSelector = new LocationSelectorImpl();
    private AllWeather weather;
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
        */
        
        // Configurata l'immagine per le impostazioni
        /* Image settingsButtonImage = new Image("/images/Settings.png");
        ImageView settingsButtonImageView = new ImageView(settingsButtonImage);
        settingsButtonImageView.setFitHeight(100);
        settingsButtonImageView.setFitHeight(100);
        settingsButtonImageView.setPreserveRatio(true);
        settingsButton.setGraphic(settingsButtonImageView); */
        
        this.weather = new AllWeather(Map.of("lat", "44.2333", "lng", "12.0500"));

        citySearchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                cityContextMenu.hide();
            }
        });

        citySearchField.addEventFilter(KeyEvent.KEY_RELEASED, this::onCitySearch);

        citySearchField.setContextMenu(cityContextMenu);

    }

    private void onCitySearch(final KeyEvent event) {
        final String query = citySearchField.getText().toLowerCase();

        if (query.isEmpty()) {
            cityContextMenu.hide();
            return;
        }

        possibleCities = locationSelector.getPossibleLocations(query);
        cityContextMenu.getItems().clear();

        if (!possibleCities.isEmpty()) {
            for (final Pair<String, Integer> city : possibleCities) {
                final MenuItem cityItem = new MenuItem(city.getX());
                cityItem.setOnAction(event1 -> updateWeatherInfo(city.getY()));
                cityContextMenu.getItems().add(cityItem);

            }
        } else {
            final MenuItem noResults = new MenuItem("Nessuna città trovata");
            cityContextMenu.getItems().add(noResults);
        }
    }

    private void updateWeatherInfo(final int cityID) {
        final Optional<Map<String, String>> locationInfoOpt = locationSelector.getByID(cityID);
        if (locationInfoOpt.isPresent()) {
            final Map<String, String> locationInfo = locationInfoOpt.get();
            weather.setLocation(locationInfo);
            if (weather.reqestsAllForecast()) {
                updateLabels();
            }
        }
    }

    private void updateLabels() {
        final Optional<Pair<String, Map<String, Number>>> currentWeatherOpt = weather.getWeatherNow();

        if (currentWeatherOpt.isPresent()) {
            final Pair<String, Map<String, Number>> currentWeatherPair = currentWeatherOpt.get();
            final Map<String, Number> currentWeather = currentWeatherPair.getY();
            currentTemperatureLabel.setText("Temperatura: " + currentWeather.get("temperature_C") + " °C");
            humidityLabel.setText("Umidità: " + currentWeather.get("humidity") + "%");
            windSpeedLabel.setText("Velocità del vento: " + currentWeather.get("wind_speed_kmh") + " km/h");
        }
    }

}
