package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class OrderDetailsController {
    @FXML
    private Label DateOfPurchaseLabel;

    @FXML
    private Label TotalPriceLabel;

    @FXML
    VBox ProductContainer;

    private String orderID;
    private Order order;
    static FireBaseManager fm = FireBaseManager.getInstance();

    public void initialize() {
        this.orderID = GlobalData.getCurrentOrderId();
        this.order = fm.getOrder(this.orderID);
        loadOrderData();
        List<Item> products = new ArrayList<>();
//        for (String itemID : this.order.getItemsID()) {
//            Item item = fm.getItem(itemID);
//            products.add(item);
//        }
        // Use a Set to track unique itemIDs
        Set<String> uniqueItemIDs = new HashSet<>();

        for (String itemID : this.order.getItemsID()) {
            if (!uniqueItemIDs.contains(itemID)) {
                uniqueItemIDs.add(itemID); // Mark itemID as processed
                Item item = fm.getItem(itemID);
                products.add(item);
            }
        }
        addProductCards(products);
    }

    // New method to load product data into the form
    private void loadOrderData() {
        if (this.order != null) {
            DateOfPurchaseLabel.setText(this.order.getDate().toString());
            TotalPriceLabel.setText(this.order.getTotalPrice());
        }
    }

    public void addProductCards(List<Item> products) {
        ProductContainer.getChildren().clear(); // Clear existing nodes

        for (Item product : products) {
            try {
                // Load the ClientProductCard.fxml
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "OrderDetailsProductCard.fxml").toURI().toURL());
                Node productCard = loader.load();

                GlobalData.setCurrentEditingProductId(product.getItemID());
                // Get the controller and set product data
                CartProductCardController controller = loader.getController();
                controller.setProductData(product.getItemName(), product.getImageURL(), this.order.getItemPrice(product), this.order.getItemQuantity(product), product.getItemID());

                // Add the product card to the VBox
                ProductContainer.getChildren().add(productCard);
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
