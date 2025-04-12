package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.Date;

public class OrderCardController {
    @FXML
    private Hyperlink OrderNumberHyperLink;

    @FXML
    private Label DateOfPurchaseLabel;

    @FXML
    private Label TotalPrice;

    private String orderID;
    private Order order;
    static FireBaseManager fm = FireBaseManager.getInstance();


    public void setOrderData(String orderNumb, String price, Date date, String orderID) {
        this.orderID = orderID;
        GlobalData.setCurrentOrderId(this.orderID);
        this.order = fm.getOrder(this.orderID);
        OrderNumberHyperLink.setText(orderNumb);
        DateOfPurchaseLabel.setText(date.toString());
        TotalPrice.setText(price);
    }

    @FXML
    public void handleOrderNumberHyperLink(ActionEvent event) throws IOException {
        System.out.println("Order Name Hyperlink Clicked");
        GlobalData.setCurrentOrderId(this.orderID);
        SceneController.switchScene(event, "OrderDetails.fxml", "Order Details");
    }


}
