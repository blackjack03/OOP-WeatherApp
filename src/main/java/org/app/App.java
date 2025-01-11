package org.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/mainview.fxml"));
        Parent root = loader.load();

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setMinHeight(900);
        primaryStage.setMinWidth(1750);

        Scene scene = new Scene(root, screenWidth * 0.8, screenHeight * 0.8);
        //Scene scene = new Scene(root);
        primaryStage.setTitle("Weather Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
