package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    static Vendor vendor;
    static Client client;
    static String vendorId = "vendorIntegration";
    static String clientId = "clientIntegration";
    static String itemId;

    static FireBaseManager fm = FireBaseManager.getInstance();

    @BeforeAll
    static void setUp() {
        User user = new User();

        // Register or get vendor
        try {
            user.Register("Vendor A", vendorId, "vendor@int.com", "pass1234", "Vendor St", "01000000001", false);
        } catch (RegistrationException ignored) {}
        GlobalData.setCurrentlyLoggedIN(vendorId);  //Set vendor login
        vendor = (Vendor) user.GetUserByID(vendorId);

        // Register or get client
        try {
            user.Register("Client B", clientId, "client@int.com", "pass1234", "Client Rd", "01000000002", true);
        } catch (RegistrationException ignored) {}
        GlobalData.setCurrentlyLoggedIN(clientId);  //Set client login
        client = (Client) user.GetUserByID(clientId);
    }

    @Test
    @Order(1)
    @DisplayName("Vendor adds item to DB")
    void vendorAddsItem() {
        GlobalData.setCurrentlyLoggedIN(vendorId);  //Switch to vendor
        assertDoesNotThrow(() -> vendor.addItem(
                "Integration Item", "Amazing Item", "Category Integration", "200.00",
                "C:/Users/amrem/Downloads/Screenshot 2025-04-09 232452.png", 10, vendorId
        ));

        assertNotNull(vendor.getItemsID(), "ItemsID list should be initialized");
        assertTrue(vendor.getItemsID().size() > 0, "ItemsID list should contain the new item");

        itemId = vendor.getItemsID().get(vendor.getItemsID().size() - 1);
    }

    @Test
    @Order(2)
    @DisplayName("Client adds item to wishlist and cart")
    void clientInteractsWithItem() {
        List<Item> item1 = fm.getItemsForVendor(vendorId);
        GlobalData.setCurrentlyLoggedIN(clientId);  //Switch to client
        assertDoesNotThrow(() -> client.addItemToWishlist(item1.getFirst().getItemID()));
        assertTrue(client.getWishlist().contains(item1.getFirst().getItemID()));



        assertDoesNotThrow(() -> client.AddToCart(item1.getFirst()));
        assertTrue(client.viewCart().contains(item1.getFirst().getItemID()));
    }

    @Test
    @Order(3)
    @DisplayName("Client submits a review for item")
    void clientSubmitsReview() {
        GlobalData.setCurrentlyLoggedIN(clientId);  //Switch to client
        assertDoesNotThrow(() -> client.addReview(itemId, 5, "Excellent!"));

        List<Review> reviews = client.fetchMyReviews();
        assertTrue(reviews.stream().anyMatch(r -> r.getItemID().equals(itemId)), "Review for item should exist");
    }

    @Test
    @Order(4)
    @DisplayName("Client places and cancels an order")
    void clientPlacesAndCancelsOrder() {
        GlobalData.setCurrentlyLoggedIN(clientId);  //Switch to client
        List<String> orders = client.getCurrentOrders();
        assertFalse(orders.isEmpty(), "Client should have at least one order");

        String orderID = orders.get(0);
        org.example.Order order = new org.example.Order();
        order.setOrderID(orderID);

        assertDoesNotThrow(() -> client.CancelOrder(order, clientId));
        assertFalse(client.getCurrentOrders().contains(orderID), "Order should be removed from current orders");
    }

    @Test
    @Order(5)
    @DisplayName("Vendor deletes item and removes associated reviews")
    void vendorDeletesItem() {
        GlobalData.setCurrentlyLoggedIN(vendorId);  //Switch to vendor
        Item item = new Item();
        item.setItemID(itemId);
        item.setVendor(vendorId);

        assertDoesNotThrow(() -> vendor.removeItem(item));
    }
}
