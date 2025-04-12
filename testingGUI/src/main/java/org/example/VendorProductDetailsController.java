package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class VendorProductDetailsController {
    @FXML
    private Label ProductNameLabel;

    @FXML
    private Label ProductPriceLabel;

    @FXML
    private Label ProductCategoryLabel;

    @FXML
    private Label ProductDescriptionLabel;

    @FXML
    private ImageView ProductImage;

    private String itemID;
    private Item item;

    private Cart cart = new Cart();
    FireBaseManager fm = FireBaseManager.getInstance();

    public void initialize() {
        this.itemID = GlobalData.getCurrentEditingProductId();
        this.item = fm.getItem(this.itemID);
        loadProductData();
    }

    // New method to load product data into the form
    private void loadProductData() {
        if (this.item != null) {
            // Pre-fill the Labels with the existing data
            ProductNameLabel.setText(this.item.getItemName());
            ProductPriceLabel.setText(this.item.getItemPrice());
            ProductCategoryLabel.setText(this.item.getItemCategory());
            ProductDescriptionLabel.setText(this.item.getItemDescription());
            ProductImage.setImage(new Image(this.item.getImageURL()));
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) throws IOException {
        System.out.println("Back Button Clicked");
        SceneController.switchScene(event, "MainVendorPage.fxml", "Products");
    }

    @FXML
    public void handleEditProduct(ActionEvent event) throws IOException {
        System.out.println("Edit Product Button Clicked");
        // Set global item ID before switching scene
        GlobalData.setCurrentEditingProductId(this.itemID);
        SceneController.switchScene(event, "EditProduct.fxml", "Edit Product");
    }

    @FXML
    public void handleDeleteProduct(ActionEvent event) throws IOException {
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
