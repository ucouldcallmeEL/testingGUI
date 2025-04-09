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
        user.setUserID(GlobalData.getCurrentlyLoggedIN());

        NameLabel.setText(user.getUserByID(GlobalData.getCurrentlyLoggedIN()));
    }

    @FXML
    private void handleAddProductButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "ProductAdd.fxml", "Add Product");
    }

    @FXML
    private void handleViewProductsButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "MainVendorPage.fxml", "Products");
    }

    @FXML
    private void handleEditProfileButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "EditProfile.fxml", "Edit Profile");
    }

    @FXML
    private void handleSignOutButton(ActionEvent event) throws IOException {
        User.SignOut();
        SceneController.switchScene(event, "Login.fxml", "Login");
    }

    @FXML
    private void handleHomeButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "MainVendorPage.fxml", "Products");
    }

}
