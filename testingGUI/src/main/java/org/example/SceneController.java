package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;

public class SceneController {
    public static void switchScene(ActionEvent event, String absolutePath, String title) throws IOException {
        // Convert to URL and load
        FXMLLoader fxmlLoader = new FXMLLoader(new java.io.File(absolutePath).toURI().toURL());
        Parent root = fxmlLoader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}
