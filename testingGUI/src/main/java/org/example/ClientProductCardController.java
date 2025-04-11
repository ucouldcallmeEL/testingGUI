package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import java.io.IOException;

public class ClientProductCardController {
    @FXML
    private Hyperlink ProductNameHyperlink;

    @FXML
    private Label ProductPriceLabel;

    @FXML
    private Label ProductStockLabel;

    @FXML
    private ImageView ProductImage;

    private String itemID;
    private Item item;
    private Cart cart = new Cart();
    FireBaseManager fm = FireBaseManager.getInstance();



    public void setProductData(String name, String image, String price, Integer stock, String itemId) {
        this.itemID = itemId;
        ProductNameHyperlink.setText(name);
        ProductPriceLabel.setText(price);
        ProductStockLabel.setText(stock.toString());
        ProductImage.setImage(new Image(image));
    }

    @FXML
    public void handleCartButton(ActionEvent event) throws IOException {
        try{
            GlobalData.setCurrentEditingProductId(this.itemID);
            this.item = fm.getItem(this.itemID);
            System.out.println("Add Product To Cart  Button Clicked");
            cart = fm.getClientCart(GlobalData.currentlyLoggedIN);
            fm.addItemToCart(GlobalData.currentlyLoggedIN, this.item, 1);

        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot add to cart. Zero stock");

        }
    }

    @FXML
    public void handleProductNameHyperlink(ActionEvent event) throws IOException {
        System.out.println("Product Name Hyperlink Clicked");
        GlobalData.setCurrentEditingProductId(this.itemID);
        SceneController.switchScene(event, "ProductDetails.fxml", "Product Details");
    }
    @FXML
    public void handleWishlistButton(ActionEvent event) throws IOException {
        System.out.println("WishList Product Button Clicked");
        fm.addItemToWishlist(GlobalData.currentlyLoggedIN, this.itemID);

    }

    @FXML
    public void handleRemoveFromCartButton (ActionEvent event) throws IOException {
        System.out.println("Remove from Cart Button Clicked");
        cart = fm.getClientCart(GlobalData.currentlyLoggedIN);
        cart.removeItem(GlobalData.currentlyLoggedIN, this.item, 1);
//        SceneController.switchScene(event, "MainPageClient.fxml", "Product Card");
    }

    @FXML
    public void handleRemoveFromWishlistButton (ActionEvent event) throws IOException {
        System.out.println("Remove from Wishlist Button Clicked");
        fm.removeItemFromWishlist(GlobalData.currentlyLoggedIN, this.itemID);
//        SceneController.switchScene(event, "MainPageClient.fxml", "Product Card");
    }
}


//public class VendorProductCardController {
//
//    @FXML
//    private Hyperlink ProductNameHyperlink;
//
//    @FXML
//    private Label ProductPriceLabel;
//
//    @FXML
//    private Label ProductStockLabel;
//
//    @FXML
//    private ImageView ProductImage;
//
//    public void setProductData(String name, String image, String price, Integer stock) {
//        ProductNameHyperlink.setText(name);
//        ProductPriceLabel.setText(price);
//        ProductStockLabel.setText(stock.toString());
//        ProductImage.setImage(new Image(image));
//    }
//
//    @FXML
//    public void handleEditProductButton(ActionEvent event) throws IOException {
//        System.out.println("Edit Product Button Clicked");
//        SceneController.switchScene(event, "EditProduct.fxml", "Edit Product");
//
//    }
//
//    @FXML
//    public void handleProductNameHyperlink(ActionEvent event) throws IOException {
//        System.out.println("Product Name Hyperlink Clicked");
//        SceneController.switchScene(event, "ProductDetails.fxml", "Product Details");
//
//    }
//}