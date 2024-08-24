package com.tsb.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("sampleApp.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
//        Parent root = FXMLLoader.load(getClass().getResource("sampleApp.fxml"));
//        stage.setTitle("Hello!");
//        stage.setScene(new Scene(root));
//        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
