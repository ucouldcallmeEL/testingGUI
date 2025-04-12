package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class OrdersController {
    @FXML
    private VBox orderContainer; // VBox where ProductCards will be added

    @FXML
    private ScrollPane scrollPane;

    public void initialize() {
        FireBaseManager fm = FireBaseManager.getInstance();
        List<Order> orders = fm.getCurrentOrdersForClient(GlobalData.currentlyLoggedIN);
        addOrdersCards(orders);

    }

    public void addOrdersCards(List<Order> orders) {
        orderContainer.getChildren().clear(); // Clear existing nodes

        for (Order order : orders) {
            try {
                // Load the ClientProductCard.fxml
                FXMLLoader loader = new FXMLLoader(new java.io.File(GlobalData.path + "OrderCard.fxml").toURI().toURL());
                Node orderCard = loader.load();

                GlobalData.setCurrentOrderId(order.getOrderID());
                // Get the controller and set product data
                OrderCardController controller = loader.getController();
                controller.setOrderData(order.getOrderID(), order.getTotalPrice(), order.getDate(), order.getOrderID());

                // Add the product card to the VBox
                orderContainer.getChildren().add(orderCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleProfileButton(ActionEvent event) throws IOException {
        System.out.println("Profile Button Clicked");
        SceneController.switchScene(event, "ClientProfile.fxml", "Profile");
    }

    @FXML
    public void handleHomeButton(ActionEvent event) throws IOException {
        System.out.println("Home Button Clicked");
        SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
    }


}
