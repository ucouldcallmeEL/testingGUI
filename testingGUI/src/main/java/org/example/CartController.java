package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartController {
    @FXML
    private VBox productContainer; // VBox where ProductCards will be added

    @FXML
    private ScrollPane scrollPane;

    List<Item> products = new ArrayList<>();
    Cart cart;

    public void initialize() {
        FireBaseManager fm = FireBaseManager.getInstance();
        this.cart = fm.getClientCart(GlobalData.currentlyLoggedIN);
        for (String itemID : cart.getItemsID()) {
            Item item = fm.getItem(itemID);
            products.add(item);
        }
        addProductCards(products);
    }

    public void addProductCards(List<Item> products) {
        productContainer.getChildren().clear(); // Clear existing nodes

        for (Item product : products) {
            try {
                // Load the ClientProductCard.fxml
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "ClientProductCard.fxml").toURI().toURL());
                Node productCard = loader.load();

                GlobalData.setCurrentEditingProductId(product.getItemID());
                // Get the controller and set product data
                ClientProductCardController controller = loader.getController();
                controller.setProductData(product.getItemName(), product.getImageURL(), product.getItemPrice(), product.getStock(), product.getItemID());

                // Add the product card to the VBox
                productContainer.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleProfileButton(ActionEvent event) throws IOException {
        System.out.println("Profile Button Clicked");
        SceneController.switchScene(event, "ClientProfile.fxml", "Profile");
    }

    @FXML
    public void handleHomeButton(ActionEvent event) throws IOException {
        System.out.println("Home Button Clicked");
        SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
    }

    @FXML
    public void handleProceedToCheckoutButton(ActionEvent event) throws IOException {
        System.out.println("Checkout Button Clicked");
        SceneController.switchScene(event, "Checkout.fxml", "Checkout");
    }
}
