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
      @FXML private Label ProductAddError;

      @FXML
      private void handlePostButton(ActionEvent event) throws IOException {
            String name = ProductNameField.getText();
            String price = ProductPriceField.getText();
            String imageURL = ProductImageURLField.getText();
            String stock = ProductStockField.getText();
            String category = ProductCategoryField.getText();
            String description = ProductDescriptionField.getText();
            FireBaseManager fm = FireBaseManager.getInstance();
            Vendor vendor = fm.getVendor(GlobalData.getCurrentlyLoggedIN());

            try{
                  System.out.println("Adding item...");
                  vendor.addItem(name, description, category, price, imageURL, Integer.valueOf(stock), GlobalData.getCurrentlyLoggedIN());
                  ProductAddError.setStyle("-fx-text-fill: green;");
                  ProductAddError.setText("Item Added Successfully!");
                  SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
//                  // Create a PauseTransition with a delay of 2 seconds
//                  PauseTransition delay = new PauseTransition(Duration.seconds(2));
//                  delay.setOnFinished(e -> {
//                        try {
//                              SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
//                        } catch (IOException ex) {
//                              ex.printStackTrace();
//                        }
//                  });
//                  delay.play();
            }
            catch(AddItemException e){
                  ProductAddError.setText(e.getMessage());
            }
            catch (IOException ex) {
                  ex.printStackTrace();
          }
      }
      @FXML
      private void handleHomeButton(ActionEvent event) throws IOException {
            System.out.println("Home Button Clicked");
            SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
      }
}
