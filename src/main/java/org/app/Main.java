// Main.java
package org.app;

import org.app.model.ConfigManager;

import javafx.application.Application;

public class Main {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    public static void main(final String[] args) {
        // 1) carico subito la config
        ConfigManager.loadConfig(CONFIG_PATH);

        // 2) lancio la splash (LoadingScreen.start)
        Application.launch(org.app.view.LoadingScreen.class, args);
        
        // Quando LoadingScreen termina, la JVM prosegue qui (e finisce il main).
    }

}



/*package org.app;
import java.security.KeyStore.LoadStoreParameter;

import org.app.model.ConfigManager;
import org.app.model.LoadingScreen;
import org.app.model.LocationSelector;

import javafx.application.Application;

public class Main {

    private final static String FILE_PATH = "src/main/java/org/files/configuration.json";

    public static void main(String[] args) {
        /*String cwd = System.getProperty("user.dir");
        System.out.println("Current working directory: " + cwd);*/

        // caricamento file di configurazione
        /*ConfigManager.loadConfig(FILE_PATH);

        Application.launch(LoadingScreen.class, args);

        final LocationSelector LS = new LocationSelector();

        // salvataggio file di configurazione
        ConfigManager.saveConfig(FILE_PATH);
    }

}*/
