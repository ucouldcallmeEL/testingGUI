package org.example;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;

public class SignUpController {
//    @FXML
//    private void handleRegisterButton(ActionEvent event) throws IOException {
//        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/MainPageClient.fxml", "Homepage");
//    }

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField userIDField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton vendorRadio;
    @FXML private Label SignUpError;
//
    @FXML
    private void handleRegisterButton(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String userID = userIDField.getText();
        String password = passwordField.getText();
        boolean isVendor = vendorRadio.isSelected();

        try {
            User user = new User();
            user.Register(name, userID, email, password, address, phone, !isVendor);

//           // Show success dialog
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Registration Successful");
//            alert.setHeaderText(null);
//            alert.setContentText("You have successfully registered!");
//            alert.showAndWait();
//            SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
            SignUpError.setStyle("-fx-text-fill: green;");
            SignUpError.setText("Registration Successful!");
            // Create a PauseTransition with a delay of 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> {
                try {
                    SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            delay.play();

        } catch (RegistrationException e) {
//            // Show error dialog
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Registration Failed");
//            alert.setHeaderText("Error during registration");
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
            SignUpError.setText(e.getMessage());
        }
    }
}
