package org.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
// import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainview.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.sizeToScene();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double calculatedW = primaryStage.getWidth();
        double calculatedH = primaryStage.getHeight();

        double finalW = Math.min(Math.max(calculatedW, 550), Math.max(1080, screenBounds.getWidth()));
        double finalH = Math.min(Math.max(calculatedH, 550), Math.max(1080, screenBounds.getHeight()));

        primaryStage.setWidth(finalW);
        primaryStage.setHeight(finalH);

        // Calcolo posizione per centrare la finestra
        double x = screenBounds.getMinX() + (screenBounds.getWidth()  - finalW) / 2;
        double y = screenBounds.getMinY() + (screenBounds.getHeight() - finalH) / 2;

        primaryStage.setX(x);
        primaryStage.setY(y);

        // Imposto i vincoli min/max
        primaryStage.setMinWidth(550);
        primaryStage.setMinHeight(550);
        primaryStage.setMaxWidth(finalW);
        primaryStage.setMaxHeight(finalH);

        primaryStage.setTitle("Weather Application");
    }

}
