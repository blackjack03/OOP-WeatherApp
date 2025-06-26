package org.app.appcore;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.app.travelmode.controller.TravelModeController;
import org.app.travelmode.controller.TravelModeControllerImpl;
import org.app.view.App;

public class MainControllerImpl implements MainController {

    private static final String TRAVEL_BUTTON_TEXT = "Passa a modalità viaggio";
    private static final String WEATHER_BUTTON_TEXT = "Passa a modalità meteo";

    private final BorderPane rootView;
    private final Parent weatherView;
    private final Parent travelModeView;
    private final TravelModeController travelModeController;

    public MainControllerImpl() {
        this.rootView = new BorderPane();

        this.weatherView = new App().getRoot();
        this.travelModeController = new TravelModeControllerImpl(this);
        this.travelModeView = this.travelModeController.gatTraveleModeView();

        final HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        final Button travelButton = new Button(TRAVEL_BUTTON_TEXT);
        final Button weatherButton = new Button(WEATHER_BUTTON_TEXT);

        travelButton.setOnAction(event -> {
            this.rootView.setCenter(this.travelModeView);
            travelButton.setDisable(true);
            weatherButton.setDisable(false);
        });
        travelButton.getStyleClass().addAll("travel-mode-button", "mode-selector-button");

        weatherButton.setOnAction(event -> {
            this.rootView.setCenter(this.weatherView);
            travelButton.setDisable(false);
            weatherButton.setDisable(true);
        });
        weatherButton.setDisable(true);
        weatherButton.getStyleClass().addAll("weather-mode-button", "mode-selector-button");

        topBar.getChildren().addAll(travelButton, weatherButton);
        topBar.getStyleClass().add("control-bar");

        this.rootView.setTop(topBar);
        this.rootView.setCenter(this.weatherView);
        this.rootView.getStyleClass().add("app-root-view");
    }

    @Override
    public Parent getRootView() {
        return this.rootView;
    }
}
