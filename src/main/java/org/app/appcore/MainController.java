package org.app.appcore;

import org.app.weathermode.controller.Controller;

import javafx.scene.Parent;

public interface MainController {

    Controller getAppController();

    Parent getRootView();

}
