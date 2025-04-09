package org.example;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.util.Duration;


import java.io.IOException;

public class LogInController {
    String absolutePath = "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/SignUp.fxml";
    static String username;
    static boolean isVendor= false;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label SignInError;
    private User user = new User();
    @FXML
    private void handleSignInButton(ActionEvent event) throws IOException {
        String userID = usernameField.getText();
        String password = passwordField.getText();

        try {
            username = user.LogIn(userID, password);
            // Show success dialog
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Login Successful");
//            alert.setHeaderText(null);
//            alert.setContentText("You have successfully LoggedIn!");
//            alert.showAndWait();
            SignInError.setStyle("-fx-text-fill: green;");
            SignInError.setText("Login Successful!");
            // Create a PauseTransition with a delay of 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            if(isVendor){
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/MainVendorPage.fxml", "Homepage");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else {

                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/MainPageClient.fxml", "Homepage");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            delay.play();
//            SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/MainPageClient.fxml", "Homepage");

        } catch (LogInException e) {
            // Show error dialog
            SignInError.setText(e.getMessage());
        }
    }

    @FXML
    private void handleSignUpButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/SignUp.fxml", "Sign Up");
    }

}