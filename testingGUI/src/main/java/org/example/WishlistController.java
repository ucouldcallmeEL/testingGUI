package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WishlistController {
    @FXML
    private VBox productContainer; // VBox where ProductCards will be added

    @FXML
    private ScrollPane scrollPane;

    List<String> wishlist = new ArrayList<>();
    List<Item> products = new ArrayList<>();

    public void initialize() {
        FireBaseManager fm = FireBaseManager.getInstance();
        this.wishlist = fm.getWishlist(GlobalData.currentlyLoggedIN);
        for (String itemID : this.wishlist) {
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
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "WishlistProductCard.fxml").toURI().toURL());
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
}
