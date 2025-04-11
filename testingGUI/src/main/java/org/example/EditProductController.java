package org.example;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;

public class EditProductController {
    private String itemID;
    private Item item;

    @FXML
    private TextField ProductNameField;
    @FXML
    private TextField ProductPriceField;
    @FXML
    private TextField ImageURLField;
    @FXML
    private TextField ProductStockField;
    @FXML
    private TextField ProductCategoryField;
    @FXML
    private TextArea ProductDescriptionField;
    @FXML
    private Label ProductUpdateError;

    FireBaseManager fm = FireBaseManager.getInstance();

    @FXML
    public void initialize() {
        this.itemID = GlobalData.getCurrentEditingProductId();
        this.item = fm.getItem(GlobalData.getCurrentEditingProductId());
        loadItemData();

//        this.itemID = GlobalData.getCurrentEditingProductId();
//
//        if (this.itemID != null) {
//            Item itemFetcher = new Item();
//            this.item = itemFetcher.getItembyID(this.itemID);            loadItemData();
//        } else {
//            System.out.println("itemID is null in EditProductController");
//        }
    }


    // New method to load item data into the form
    private void loadItemData() {
        if (this.item != null) {
            // Fetch existing data from the item
            String existingProductName = this.item.getItemName();
            String existingProductPrice = this.item.getItemPrice();
            String existingImageURL = this.item.getImageURL();
            Integer existingProductStock = this.item.getStock();
            String existingProductCategory = this.item.getItemCategory();
            String existingProductDescription = this.item.getItemDescription();

            // Pre-fill the TextFields with the existing data
            ProductNameField.setText(existingProductName);
            ProductPriceField.setText(existingProductPrice);
            ImageURLField.setText(existingImageURL);
            ProductStockField.setText(existingProductStock.toString());
            ProductCategoryField.setText(existingProductCategory);
            ProductDescriptionField.setText(existingProductDescription);
        }
    }

    @FXML
    private void handleUpdateButton(ActionEvent event) throws IOException {
        String name = ProductNameField.getText();
        String price = ProductPriceField.getText();
        String imageURL = ImageURLField.getText();
        String stock = ProductStockField.getText();
        String category = ProductCategoryField.getText();
        String description = ProductDescriptionField.getText();

        Vendor vendor = fm.getVendor(GlobalData.getCurrentlyLoggedIN());
        try {
            this.item.changeItemName(this.itemID, name);
            this.item.changePrice(this.itemID, price);
            this.item.changeImageURL(this.itemID, imageURL);
            this.item.updateStock(Integer.valueOf(stock));
            this.item.changeCategory(this.itemID, category);
            this.item.changeDescription(this.itemID, description);

            ProductUpdateError.setStyle("-fx-text-fill: green;");
            ProductUpdateError.setText("Update Successful!");
            SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
//        } catch (UpdateException e) {
//            ProductUpdateError.setText(e.getMessage());
        } catch (IOException ex) {
            ProductUpdateError.setText(ex.getMessage());
        } catch (Exception ex) {
            ProductUpdateError.setText(ex.getMessage());
        }
    }

    @FXML
    public void handleHomeButton(ActionEvent event) throws IOException {
        System.out.println("Home Button Clicked");
        SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
    }

    @FXML
    public void handleDeleteProductButton(ActionEvent event) throws IOException {
        try{
            GlobalData.setCurrentEditingProductId(this.itemID);
            this.item = fm.getItem(this.itemID);
            System.out.println("Delete Product Button Clicked");
            fm.deleteItem(GlobalData.currentEditingProductId,GlobalData.currentlyLoggedIN);
            SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot add to cart. Zero stock");

        }

    }
}