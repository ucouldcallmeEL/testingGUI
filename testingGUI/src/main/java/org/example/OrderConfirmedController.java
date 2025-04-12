package org.example;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class OrderConfirmedController {

    @FXML
    private void handleCloseButton(ActionEvent event) {
        // Close the popup window
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}