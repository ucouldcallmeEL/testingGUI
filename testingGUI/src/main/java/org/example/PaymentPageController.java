package org.example;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class PaymentPageController {
    @FXML private TextField CardNumberTextField;
    @FXML private TextField CVVTextField;
    @FXML private Label PaymentErrorLabel;
    private Exception exception;

    private Cart cart;

    public void setCart(Cart cart) {
        this.cart = cart;
    }


    public Exception getException() {
        return exception;
    }

//    @FXML
//    public void handleDoneButton(ActionEvent event){
//        String cardNumber = CardNumberTextField.getText();
//        String cvv = CVVTextField.getText();
//        // Assuming you have a method to process the payment
//        // processPayment(cardNumber, cvv);
//        try{
//            PaymentProcessor paymentController = new PaymentProcessor(LogInController.username, cardNumber, cvv);
//            paymentController.isCardValid();
////            Alert alert = new Alert(Alert.AlertType.INFORMATION);
////            alert.setTitle("Payment Successful");
////            alert.setHeaderText(null);
////            alert.setContentText("Payment was successful!");
//            this.cart.confirmOrder();
//            SceneController.Popup(event, "OrderConfirmed.fxml", "Order Confirmed");
//            SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
//
//
//        } catch(PaymentException e){
//            PaymentErrorLabel.setText(e.getMessage());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (ZeroStockException e) {
//            e.printStackTrace();
//        } catch (UpdateException e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    public void handleDoneButton(ActionEvent event) throws PaymentException, IOException, ZeroStockException, UpdateException {
        try {
            String cardNumber = CardNumberTextField.getText();
            String cvv = CVVTextField.getText();

            PaymentProcessor paymentController = new PaymentProcessor(LogInController.username, cardNumber, cvv);
            paymentController.isCardValid();

            // Confirm the order
            this.cart.confirmOrder();

            // Close the pop-up after successful payment
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (PaymentException e) {
            PaymentErrorLabel.setText(e.getMessage());
        } catch (Exception e) {
            this.exception = e; // Store the exception to be retrieved later
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) throws IOException {
        System.out.println("Back Button Clicked");
        // Close the pop-up after successful payment
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
