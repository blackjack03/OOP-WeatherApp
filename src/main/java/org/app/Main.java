// Main.java
package org.app;

import org.app.weathermode.model.ConfigBuilder;
import org.app.weathermode.model.ConfigManager;

import javafx.application.Application;

public class Main {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";


    public static void main(final String[] args) {
        // 0) controllo che la configurazione esista
        // ed eventualmente la creo
        ConfigBuilder.createConfigIfNotExists(CONFIG_PATH);

        // 1) carico subito la config
        ConfigManager.loadConfig(CONFIG_PATH);

        // 2) lancio la splash (LoadingScreen.start)
        //Application.launch(org.app.view.LoadingScreen.class, args);

        Application.launch(AppCore.class, args);
    }

}
