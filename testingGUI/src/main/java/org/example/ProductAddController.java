package org.example;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;

public class ProductAddController {

      @FXML private TextField ProductNameField;
      @FXML private TextField ProductPriceField;
      @FXML private TextField ProductImageURLField;
      @FXML private TextField ProductStockField;
      @FXML private TextField ProductCategoryField;
      @FXML private TextArea ProductDescriptionField;
      @FXML private Button PostButton;
      @FXML private Label ProductAddError;

      @FXML
      private void handlePostButton(ActionEvent event){
            String name = ProductNameField.getText();
            String price = ProductPriceField.getText();
            String imageURL = ProductImageURLField.getText();
            String stock = ProductStockField.getText();
            String category = ProductCategoryField.getText();
            String description = ProductDescriptionField.getText();

            Vendor vendor = new Vendor();

            try{
                  vendor.addItem(name, description, category, price, imageURL, Integer.valueOf(stock), GlobalData.getCurrentlyLoggedIN());
                  ProductAddError.setStyle("-fx-text-fill: green;");
                  ProductAddError.setText("Registration Successful!");
                  // Create a PauseTransition with a delay of 2 seconds
                  PauseTransition delay = new PauseTransition(Duration.seconds(2));
                  delay.setOnFinished(e -> {
                        try {
                              SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
                        } catch (IOException ex) {
                              ex.printStackTrace();
                        }
                  });
                  delay.play();
            }
            catch(AddItemException e){
                  ProductAddError.setText(e.getMessage());
            }
      }



}
