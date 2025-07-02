// Main.java
package org.app;

import org.app.launcher.AppCore;
import org.app.weathermode.view.CustomErrorGUI;

import java.io.IOException;

import org.app.config.ConfigBuilder;
import org.app.config.ConfigManager;

import javafx.application.Application;

public class Main {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    public static void main(final String[] args) {
        // 0) controllo che la configurazione esista
        // ed eventualmente la creo
        try {
            ConfigBuilder.createConfigIfNotExists(CONFIG_PATH);
        } catch (final IOException e) {
            System.err.println("Errore durante la creazione del file di configurazione: "
                + e.getMessage());
            CustomErrorGUI.showError("Errore durante la creazione del file di configurazione",
                "Errore File Configurazione");
            return;
        }

        // 1) carico subito la config
        ConfigManager.loadConfig(CONFIG_PATH);

        // 2) lancio AppCore
        Application.launch(AppCore.class, args);
    }

}
