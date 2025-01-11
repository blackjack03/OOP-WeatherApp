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

        
        primaryStage.setMinHeight(550);
        primaryStage.setMinWidth(550);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Weather Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
