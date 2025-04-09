package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class MainVendorPageController {


    @FXML
    private VBox productContainer; // VBox where ProductCards will be added

    @FXML
    private ScrollPane scrollPane;


    public void initialize() {

        List<Item> products = Vendor.getItemsForVendor(LogInController.username);

        addProductCards(products);
    }

    public void addProductCards(List<Item> products) {
//        String absolutePath = "MainVendorPage.fxml";

        productContainer.getChildren().clear(); // Clear existing nodes

        for (Item product : products) {
            try {
                // Load the VendorProductCard.fxml
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "VendorProductCard.fxml").toURI().toURL());
                Node productCard = loader.load();

                // Get the controller and set product data
                VendorProductCardController controller = loader.getController();
                controller.setProductData(product.getItemName(), product.getImageURL(), product.getItemPrice(), product.getStock(), product.getItemID());

                // Add the product card to the VBox
                productContainer.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddProductButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "ProductAdd.fxml", "Add Product");
    }

    @FXML
    private void handleVendorProfileButton(ActionEvent event) throws IOException {
        SceneController.switchScene(event, "VendorProfile.fxml", "Profile");
    }
}