package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Replace with your actual absolute path


        String absolutePath = "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/LogIn.fxml";

        // Convert to URL and load
        FXMLLoader fxmlLoader = new FXMLLoader(new java.io.File(absolutePath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}