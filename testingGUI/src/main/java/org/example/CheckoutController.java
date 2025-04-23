package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckoutController {

    @FXML
    private VBox productContainer; // VBox where ProductCards will be added

    @FXML
    private RadioButton CODRadioButton;
    @FXML
    private RadioButton CreditCardRadioButton;
    @FXML
    private Label PriceLabel;

    @FXML
    private Label CheckoutErrorLabel;

    List<Item> products = new ArrayList<>();
    Cart cart;

    static FireBaseManager fm = FireBaseManager.getInstance();

    public void initialize() {
        this.cart = fm.getClientCart(GlobalData.currentlyLoggedIN);
        PriceLabel.setText(this.cart.getTotalPrice());

//        for (String itemID : cart.getItemsID()) {
//            Item item = fm.getItem(itemID);
//            products.add(item);
//        }
        // Use a Set to track unique itemIDs
        Set<String> uniqueItemIDs = new HashSet<>();

        for (String itemID : cart.getItemsID()) {
            if (!uniqueItemIDs.contains(itemID)) {
                uniqueItemIDs.add(itemID); // Mark itemID as processed
                Item item = fm.getItem(itemID);
                products.add(item);
            }
        }
        addProductCards(products);
        // Create a ToggleGroup
        ToggleGroup paymentGroup = new ToggleGroup();

        // Add radio buttons to the group
        CODRadioButton.setToggleGroup(paymentGroup);
        CreditCardRadioButton.setToggleGroup(paymentGroup);

        // Optionally, set a default selection
        CODRadioButton.setSelected(true);
//        this.isCOD = CODRadioButton.isSelected();
//        this.isCreditCard = CreditCardRadioButton.isSelected();
    }

    public void addProductCards(List<Item> products) {
        productContainer.getChildren().clear(); // Clear existing nodes

        for (Item product : products) {
            try {
                // Load the ClientProductCard.fxml
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "CartProductCard.fxml").toURI().toURL());
                Node productCard = loader.load();

                GlobalData.setCurrentEditingProductId(product.getItemID());
                // Get the controller and set product data
                CartProductCardController controller = loader.getController();
                controller.setProductData(product.getItemName(), product.getImageURL(), this.cart.getItemPrice(product), this.cart.getItemQuantity(product), product.getItemID());

                // Add the product card to the VBox
                productContainer.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleCartButton(ActionEvent event) throws IOException {
        System.out.println("Back Button Clicked");
        SceneController.switchScene(event, "Cart.fxml", "Cart");
    }

//    @FXML
//    public void handleProceedToPaymentButton(ActionEvent event) throws IOException {
//        if (CODRadioButton.isSelected()) {
//            System.out.println("Cash on Delivery selected");
//            try {
//                this.cart.confirmOrder();
//                SceneController.Popup(event, "OrderConfirmed.fxml", "Order Confirmed");
//                SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
//
//            } catch (ZeroStockException | UpdateException e) {
//                CheckoutErrorLabel.setText(e.getMessage());
//            }
//            // Handle Cash on Delivery logic here
//        } else if (CreditCardRadioButton.isSelected()) {
//            System.out.println("Credit Card selected");
//            // Load the PaymentPage.fxml
//            FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "PaymentPage.fxml").toURI().toURL());
//            Parent root = loader.load();
//            // Get the controller and pass the cart
//            PaymentPageController controller = loader.getController();
//            controller.setCart(this.cart);
//            // Switch to the payment page
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setScene(scene);
//            stage.setTitle("Payment Processing");
//            stage.show();
//        } else {
//            System.out.println("No payment method selected");
//        }
//        System.out.println("Proceed to Payment Button Clicked");
//    }

    @FXML
    public void handleProceedToPaymentButton(ActionEvent event) throws IOException{
        if (CODRadioButton.isSelected()) {
            System.out.println("Cash on Delivery selected");
            try {
                this.cart.confirmOrder();
                SceneController.Popup(event, "OrderConfirmed.fxml", "Order Confirmed");
                SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
            } catch (ZeroStockException | UpdateException | ChangeException e) {
                CheckoutErrorLabel.setText(e.getMessage());
            }
        } else if (CreditCardRadioButton.isSelected()) {
            System.out.println("Credit Card selected");
            try {
                // Load the PaymentPage.fxml
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "PaymentPage.fxml").toURI().toURL());
                Parent root = loader.load();

                // Get the controller and pass the cart
                PaymentPageController controller = loader.getController();
                controller.setCart(this.cart);
                controller.setOriginalEvent(event); // Pass the ActionEvent

                // Create a new stage for the pop-up
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Payment Processing");
                popupStage.setScene(new Scene(root));
                popupStage.showAndWait(); // Wait for the pop-up to close
                // Check if an exception occurred in the PaymentPageController
                if (controller.getException() != null) {
                    throw controller.getException();
                }
            } catch (PaymentException | ZeroStockException | UpdateException e){
            CheckoutErrorLabel.setText(e.getMessage());
            } catch (IOException e) {
                CheckoutErrorLabel.setText("Failed to load the payment page.");
                e.printStackTrace();
            } catch (Exception e) {
                CheckoutErrorLabel.setText("An unexpected error occurred.");
                e.printStackTrace();
            }
        } else {
            System.out.println("No payment method selected");
        }
        System.out.println("Proceed to Payment Button Clicked");
    }
}
