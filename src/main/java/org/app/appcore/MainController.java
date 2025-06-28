package org.app.appcore;

import org.app.weathermode.controller.Controller;

import javafx.scene.Parent;

/**
 * Defines the contract for the main controller of the application.
 *
 * <p>This interface provides the essential methods for managing the application's
 * root view and accessing the weather mode controller. It serves as the primary
 * control point for the application's main functionality and view management.
 */
public interface MainController {

    /**
     * Retrieves the controller responsible for weather mode functionality.
     *
     * <p>This method provides access to the weather mode controller, which manages
     * all weather-related features and interactions within the application.
     *
     * @return the {@link Controller} instance managing weather mode functionality
     */
    Controller getAppController();

    /**
     * Retrieves the root view of the application.
     *
     * <p>This method returns the top-level visual component that contains
     * all other UI elements of the application. The returned Parent node
     * represents the main container for the application's user interface.
     *
     * @return the root {@link Parent} node of the application's view hierarchy
     */
    Parent getRootView();

}
