package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import java.io.IOException;


public class PaymentPageController {
    @FXML private TextField CardNumberTextField;
    @FXML private TextField CVVTextField;
    @FXML private Label PaymentErrorLabel;

    private Cart cart;

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @FXML
    public void handleDoneButton(ActionEvent event){
        String cardNumber = CardNumberTextField.getText();
        String cvv = CVVTextField.getText();
        // Assuming you have a method to process the payment
        // processPayment(cardNumber, cvv);
        try{
            PaymentProcessor paymentController = new PaymentProcessor(LogInController.username, cardNumber, cvv);
            paymentController.isCardValid();
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Payment Successful");
//            alert.setHeaderText(null);
//            alert.setContentText("Payment was successful!");
            this.cart.confirmOrder();
            SceneController.Popup(event, "OrderConfirmed.fxml", "Order Confirmed");
            SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");


        } catch(PaymentException e){
            PaymentErrorLabel.setText(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ZeroStockException e) {
            e.printStackTrace();
        } catch (UpdateException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) throws IOException {
        System.out.println("Back Button Clicked");
        SceneController.switchScene(event, "Checkout.fxml", "Checkout");
    }
}
