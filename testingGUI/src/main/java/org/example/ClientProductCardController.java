package org.example;

import javafx.fxml.FXML;
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

    public void initialize(String itemID){
        this.itemID = itemID;

    }
    public void setProductData(String name, String image, String price, Integer stock) {
        ProductNameHyperlink.setText(name);
        ProductPriceLabel.setText(price);
        ProductStockLabel.setText(stock.toString());
        ProductImage.setImage(new Image(image));
    }

//    public void handleCartButton(ActionEvent event) {
//        try{
//            FireBaseManager fm = FireBaseManager.getInstance();
//            Item item = new Item();
//            item = item.getItembyID(this.itemID);
//            Cart cart = new Cart();
//            cart.addItem(LogInController.username, this.itemID , 1);
//
//        }
//        catch(){
//
//        }
//    }
//
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