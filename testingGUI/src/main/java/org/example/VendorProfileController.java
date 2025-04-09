package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;


public class VendorProfileController {
    @FXML
    private Label NameLabel;
    
    public void initialize(){
        User user = new User();
        user.setUserID(LogInController.username);

        NameLabel.setText(user.getName());
    }

    @FXML
    private void handleAddProductButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/ProductAdd.fxml", "Add Product");
    }

    @FXML
    private void handleViewProductsButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/MainVendorPage.fxml", "Products");
    }

    @FXML
    private void handleEditProfileButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/EditProfile.fxml", "Edit Profile");
    }

    @FXML
    private void handleSignOutButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/Login.fxml", "Login");
    }

    @FXML
    private void handleHomeButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/MainVendorPage.fxml", "Products");
    }

}
