package org.app.appcore;

import org.app.weathermode.controller.AppController;

import javafx.scene.Parent;

public interface MainController {

    AppController getAppController();

    Parent getRootView();

}
