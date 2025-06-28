// Main.java
package org.app;

import org.app.launcher.AppCore;
import org.app.config.ConfigBuilder;
import org.app.config.ConfigManager;

import javafx.application.Application;
import java.awt.Toolkit;

public class Main {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    public static void main(final String[] args) {
        // 0) controllo che la configurazione esista
        // ed eventualmente la creo
        ConfigBuilder.createConfigIfNotExists(CONFIG_PATH);

        // 1) carico subito la config
        ConfigManager.loadConfig(CONFIG_PATH);

        // Toolkit.getDefaultToolkit();
        java.awt.Toolkit.getDefaultToolkit();

        // 2) lancio la splash (LoadingScreen.start)
        Application.launch(AppCore.class, args);
    }

}
