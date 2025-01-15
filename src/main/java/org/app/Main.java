package org.app;

import org.app.model.ConfigManager;

import javafx.application.Application;

public class Main {

    private final static String FILE_PATH = "src/main/java/org/files/configuration.json";

    public static void main(String[] args) {
        /*String cwd = System.getProperty("user.dir");
        System.out.println("Current working directory: " + cwd);*/

        // caricamento file di configurazione
        ConfigManager.loadConfig(FILE_PATH);
        
        Application.launch(App.class, args);

        // salvataggio file di configurazione
        ConfigManager.saveConfig(FILE_PATH);
    }

}
