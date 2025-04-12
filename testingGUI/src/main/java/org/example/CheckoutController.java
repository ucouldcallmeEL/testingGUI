package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {

    @FXML
    private VBox productContainer; // VBox where ProductCards will be added

    @FXML
    private RadioButton CODRadioButton;
    @FXML
    private RadioButton CreditCardRadioButton;

    List<Item> products = new ArrayList<>();
    Cart cart;

    private boolean isCOD;
    private boolean isCreditCard;

    public void initialize() {
        FireBaseManager fm = FireBaseManager.getInstance();
        this.cart = fm.getClientCart(GlobalData.currentlyLoggedIN);
        for (String itemID : cart.getItemsID()) {
            Item item = fm.getItem(itemID);
            products.add(item);
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
                controller.setProductData(product.getItemName(), product.getImageURL(), product.getItemPrice(), product.getStock(), product.getItemID());

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

    @FXML
    public void handleProceedToPaymentButton(ActionEvent event) throws IOException {
        if (CODRadioButton.isSelected()) {
            System.out.println("Cash on Delivery selected");
            try {
                this.cart.confirmOrder();
                SceneController.Popup(event, "OrderConfirmed.fxml", "Order Confirmed");
                SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");

            } catch (ZeroStockException e) {
                throw new RuntimeException(e);
            } catch (UpdateException e) {
                throw new RuntimeException(e);
            }

            // Handle Cash on Delivery logic here
        } else if (CreditCardRadioButton.isSelected()) {
            System.out.println("Credit Card selected");
            // Handle Credit Card logic here
            SceneController.switchScene(event, "PaymentPage.fxml", "Payment Processing");

        } else {
            System.out.println("No payment method selected");
        }
        System.out.println("Proceed to Payment Button Clicked");
    }
}
