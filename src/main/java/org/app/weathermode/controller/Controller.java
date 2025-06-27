package org.app.weathermode.controller;

import org.app.weathermode.model.AllWeather;
import org.app.weathermode.view.App;

public interface Controller {

    void start();

    App getApp();

    AllWeather getWeatherObj();

    void forceRefresh();

    void stop();

}
