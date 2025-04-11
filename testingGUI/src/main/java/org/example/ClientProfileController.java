package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class ClientProfileController {
    @FXML
    private Label ClientNameLabel;

    public void initialize(){
        User user = new User();
        user.setUserID(GlobalData.getCurrentlyLoggedIN());

        ClientNameLabel.setText(user.getUserByID(GlobalData.getCurrentlyLoggedIN()));
    }

    @FXML
    private void handleCartButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "Cart.fxml", "Cart");
    }

    @FXML
    private void handleWishlistButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "Wishlist.fxml", "Wishlist");
    }

    @FXML
    private void handleOrdersButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "Orders.fxml", "Orders");
    }

    @FXML
    private void handleEditProfileBotton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "EditProfile.fxml", "Edit Profile");
    }

    @FXML
    private void handleSignOutButton(ActionEvent event) throws IOException {
        User.SignOut();
        SceneController.switchScene(event, "Login.fxml", "Login");
    }

    @FXML
    private void handleHomeButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
    }

}
