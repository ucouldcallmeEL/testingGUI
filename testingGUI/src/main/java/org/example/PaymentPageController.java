package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class PaymentPageController {
    @FXML private TextField CardNumber;
    @FXML private TextField CVV;
    @FXML private Label PaymentErrorLabel;

    @FXML
    public void handleDoneButton(ActionEvent event){
        String cardNumber = CardNumber.getText();
        String cvv = CVV.getText();
        // Assuming you have a method to process the payment
        // processPayment(cardNumber, cvv);
        try{
            PaymentController paymentController = new PaymentController(LogInController.username, cardNumber, cvv);
            paymentController.isCardValid();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Payment Successful");
            alert.setHeaderText(null);
            alert.setContentText("Payment was successful!");


        }catch(PaymentException e){
            PaymentErrorLabel.setText(e.getMessage());
        }
    }
}
