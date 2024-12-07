package org.app.travelmode;

import javafx.application.Application;
import org.app.travelmode.controller.TravelModeControllerImpl;
import org.app.travelmode.model.PlaceAutocompleteImpl;

public class MainTest {
    public static void main(String[] args) {
        Application.launch(TravelModeControllerImpl.class, args);
    }
}
