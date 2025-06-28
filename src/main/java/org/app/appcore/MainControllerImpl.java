package org.app.appcore;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.app.config.ConfigManager;
import org.app.travelmode.controller.TravelModeController;
import org.app.travelmode.controller.TravelModeControllerImpl;
import org.app.weathermode.controller.AppController;
import org.app.weathermode.controller.Controller;

/**
 * Main controller implementation that manages the application's root view and mode switching functionality.
 *
 * <p>This controller handles the switching between weather and travel modes of the application,
 * managing their respective views and controllers. It implements a top bar with mode selection
 * buttons and maintains the root layout of the application.
 *
 * <p>The controller initializes with the weather mode active by default and provides functionality
 * to switch between modes. When switching to travel mode for the first time, it ensures the
 * Google API key is available.
 */
public class MainControllerImpl implements MainController {

    private static final String TRAVEL_BUTTON_TEXT = "Passa a modalità viaggio";
    private static final String WEATHER_BUTTON_TEXT = "Passa a modalità meteo";

    private final BorderPane rootView;
    private final Parent weatherView;
    private final Parent travelModeView;
    private final TravelModeController travelModeController;
    private boolean isTravelModeFirstUse = true;

    private final Controller appController;

    /**
     * Constructs a new MainControllerImpl instance.
     *
     * <p>Initializes the root view with a top control bar containing mode selection buttons
     * and sets up the initial weather view.
     */
    public MainControllerImpl() {
        this.rootView = new BorderPane();

        this.appController = new AppController();
        this.weatherView = this.appController.getApp().getRoot();

        this.travelModeController = new TravelModeControllerImpl(this);
        this.travelModeView = this.travelModeController.gatTraveleModeView();

        final HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        final Button travelButton = new Button(TRAVEL_BUTTON_TEXT);
        final Button weatherButton = new Button(WEATHER_BUTTON_TEXT);

        travelButton.setOnAction(event -> {
            if (this.isTravelModeFirstUse) {
                this.isTravelModeFirstUse = false;
                this.lookForGoogleApiKey();
                this.travelModeController.startTravelMode();
            }
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

        topBar.getChildren().addAll(weatherButton, travelButton);
        topBar.getStyleClass().add("control-bar");

        this.rootView.setTop(topBar);
        this.rootView.setCenter(this.weatherView);
        this.rootView.getStyleClass().add("app-root-view");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Controller getAppController() {
        return this.appController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parent getRootView() {
        return this.rootView;
    }

    /**
     * Checks for the presence of a Google API key in the configuration.
     *
     * <p>If no API key is found, triggers the API key request dialog through
     * the weather mode controller. This method is called when travel mode
     * is activated for the first time.
     */
    private void lookForGoogleApiKey() {
        if (ConfigManager.getConfig().getApi().getApiKey().isEmpty()) {
            this.appController.requestGoogleApiKey();
        }
    }
}
