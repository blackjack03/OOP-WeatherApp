package org.app.appcore;

import javafx.scene.Parent;

import org.app.controller.AppController;

public interface MainController {

    AppController getAppController();

    Parent getRootView();

}
