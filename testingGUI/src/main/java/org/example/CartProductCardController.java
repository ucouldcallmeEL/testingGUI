package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class CartProductCardController {
    @FXML
    private Hyperlink ProductNameHyperlink;

    @FXML
    private Label ProductPriceLabel;

    @FXML
    private Label ProductQuantityLabel;

    @FXML
    private ImageView ProductImage;

    private String itemID;
    private Item item;
    private Cart cart = new Cart();
    FireBaseManager fm = FireBaseManager.getInstance();


    public void setProductData(String name, String image, String price, Integer stock, String itemId) {
        this.itemID = itemId;
        GlobalData.setCurrentEditingProductId(this.itemID);
        this.item = fm.getItem(this.itemID);
        ProductNameHyperlink.setText(name);
        ProductPriceLabel.setText(price);
        ProductQuantityLabel.setText("1");
        ProductImage.setImage(new Image(image));
    }

    @FXML
    public void handleProductNameHyperlink(ActionEvent event) throws IOException {
        System.out.println("Product Name Hyperlink Clicked");
        GlobalData.setCurrentEditingProductId(this.itemID);
        SceneController.switchScene(event, "ProductDetails.fxml", "Product Details");
    }

    @FXML
    public void handleRemoveFromCartButton(ActionEvent event) throws IOException {
//        GlobalData.setCurrentEditingProductId(this.itemID);
//        this.item = fm.getItem(this.itemID);
        System.out.println("Remove from Cart Button Clicked");
        cart = fm.getClientCart(GlobalData.currentlyLoggedIN);
//        cart.removeItem(GlobalData.currentlyLoggedIN, this.item, 1);
        fm.removeItemFromCart(GlobalData.currentlyLoggedIN, this.item, 1);
//        SceneController.switchScene(event, "MainPageClient.fxml", "Product Card");
    }

    @FXML
    public void handleMoveToWishlistButton(ActionEvent event) throws IOException {
        System.out.println("Move To Wishlist Button Clicked");
        fm.removeItemFromCart(GlobalData.currentlyLoggedIN, this.item, 1);
        fm.addItemToWishlist(GlobalData.currentlyLoggedIN, this.itemID);
    }
}
