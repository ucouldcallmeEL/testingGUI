package org.example;

import org.junit.jupiter.api.*;
import java.util.*;
import org.junit.jupiter.api.Order;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderTest {

    private org.example.Order order;
    private Item sampleItem;

    @BeforeEach
    void setUp() {
        ArrayList<String> itemIDs = new ArrayList<>(Arrays.asList("item123", "item123", "item456"));
        order = new org.example.Order("order001", "user001", itemIDs, true, "150.00");

        sampleItem = new Item();
        sampleItem.setItemID("item123");
        sampleItem.setItemPrice("50.00");
    }

    @Test
    @Order(1)
    @DisplayName("Constructor initializes all fields correctly")
    void constructor_InitializesFields() {
        assertEquals("order001", order.getOrderID());
        assertEquals("user001", order.getUserID());
        assertEquals(3, order.getItemsID().size());
        assertTrue(order.isCurrent());
        assertEquals("150.00", order.getTotalPrice());
        assertNotNull(order.getDate());
    }

    @Test
    @Order(2)
    @DisplayName("Setters and Getters work as expected")
    void settersAndGetters_WorkCorrectly() {
        Date now = new Date();
        order.setOrderID("order002");
        order.setUserID("user002");
        ArrayList<String> newItems = new ArrayList<>(List.of("item789"));
        order.setItemsID(newItems);
        order.setCurrent(false);
        order.setTotalPrice("200.00");
        order.setDate(now);

        assertEquals("order002", order.getOrderID());
        assertEquals("user002", order.getUserID());
        assertEquals(newItems, order.getItemsID());
        assertFalse(order.isCurrent());
        assertEquals("200.00", order.getTotalPrice());
        assertEquals(now, order.getDate());
    }

    @Test
    @Order(3)
    @DisplayName("Get item quantity returns correct count")
    void getItemQuantity_ReturnsCorrectCount() {
        int quantity = order.getItemQuantity(sampleItem);
        assertEquals(2, quantity);
    }

    @Test
    @Order(4)
    @DisplayName("Get item price returns correct total")
    void getItemPrice_ReturnsCorrectTotal() {
        String totalItemPrice = order.getItemPrice(sampleItem);
        assertEquals("100.0", totalItemPrice);
    }

    @Test
    @Order(5)
    @DisplayName("GetOrderDetails returns correct summary for DB order")
    void getOrderDetails_ReturnsSummary() {
        String orderID = "3sFMtWpLpKsTJoHQyuq4"; // Must exist in your Firestore
        org.example.Order order = FireBaseManager.getInstance().getOrder(orderID);

        assertNotNull(order, "Order should be retrieved from Firestore");

        String details = order.GetOrderDetails();

        // Validate fields dynamically to avoid hardcoding
        assertTrue(details.contains("Order ID: " + orderID));
        assertTrue(details.contains("User ID: " + order.getUserID()));
        assertTrue(details.contains("Items ID:"));
        assertTrue(details.contains("Current: " + (order.isCurrent() ? "Yes" : "No")));
    }


    @Test
    @Order(6)
    @DisplayName("ConfirmOrder calls makeHistory without exception")
    void confirmOrder_CallsMakeHistory() {
        assertDoesNotThrow(() -> order.ConfirmOrder("0KwTUNUDACq9uvuRZoFL", "hagar"));
    }

    @Test
    @Order(7)
    @DisplayName("getItemQuantity returns 0 for item not in list")
    void getItemQuantity_ItemNotInOrder_ReturnsZero() {
        Item otherItem = new Item();
        otherItem.setItemID("item999");
        int qty = order.getItemQuantity(otherItem);
        assertEquals(0, qty);
    }
}
