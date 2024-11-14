package org.app.travelmode.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import org.app.travelmode.view.TravelModeView;
import org.app.travelmode.view.TravelModeViewImpl;

public class TravelModeControllerImpl extends Application implements TravelModeController {

    private final TravelModeView view = new TravelModeViewImpl();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.view.start();
    }
}
