package org.example;

import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.List;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WhiteBoxTest {
    static FireBaseManager fm=FireBaseManager.getInstance();
    private static final String testUserID = "testClient1";
    private static final String baseEmail = "testuser@gmail.com";
    private static final String basePassword = "password123";
    private static Client user;
    private static Cart cart;
    private static Item item;
    private static org.example.Order order;

    @BeforeAll
    static void setUp() {
        // Initialize objects or mock dependencies
        user = fm.getClient(testUserID);
        cart = fm.getClientCart(testUserID);
        item = fm.getItem("Aq5uyCPe3xhM1ZMFQIrt");
       // org.example.Order order = new org.example.Order();
    }
    @Test
    @Order(1)
    @DisplayName("Register a new client successfully")
    void registerClient_NewUser_Success() {
        String uniqueID = "testClient1";
        try {
            // If the user already exists (from previous runs), skip registration
            user.LogIn(uniqueID, basePassword); // this will throw if not registered
        } catch (LogInException e) {
            // User doesn't exist yet → proceed with registration
            assertDoesNotThrow(() ->
                    user.Register("New User", uniqueID, "new" + baseEmail, basePassword, "123 Street", "01094749270", true)
            );
        }
    }



    @Test
    @Order(2)
    @DisplayName("Login, Add Items to Cart, Confirm Order")
    void testLoginAddToCartAndConfirmOrder() {
        // Step 1: Log in as a user
        assertDoesNotThrow(() -> user.LogIn(testUserID, basePassword), "Login should succeed");

        // Step 2: Add items to the cart


        Item TestItem = item.getItembyID("Aq5uyCPe3xhM1ZMFQIrt");
        int stock=TestItem.getStock();
        int quantity=1;
        assertDoesNotThrow(() -> cart.addItem(GlobalData.getCurrentlyLoggedIN(), TestItem, quantity), "Item should be added to cart");

        // Assert that the item was added
        assertTrue(cart.getItemsID().contains("Aq5uyCPe3xhM1ZMFQIrt"), "Cart should contain the added item");
        assertEquals(cart.getItemPrice(TestItem),cart.getTotalPrice());
        // Step 3: Confirm the order
        assertDoesNotThrow(() -> cart.confirmOrder(), "Order confirmation should succeed");
        assertEquals(stock-quantity,TestItem.fetchStockFromDB(), "Expected Stock should be equal to actual stock");

        // Validate order details
        // assertEquals("order001", order.getOrderID(), "Order ID should match");
    }
    @Test
    @Order(3)
    @DisplayName("add Bulk Order (Stress Test)")
    void testAddItemsToCart() {
        assertDoesNotThrow(() -> user.LogIn(testUserID, basePassword), "Login should succeed");

        // Step 2: Add items to the cart


        Item TestItem = item.getItembyID("Aq5uyCPe3xhM1ZMFQIrt");
        int stock=TestItem.getStock();
        int quantity=800;
        assertDoesNotThrow(() -> cart.addItem(GlobalData.getCurrentlyLoggedIN(), TestItem, quantity), "Item should be added to cart");

        // Assert that the item was added
        assertTrue(cart.getItemsID().contains("Aq5uyCPe3xhM1ZMFQIrt"), "Cart should contain the added item");
        assertEquals(cart.getItemPrice(TestItem),cart.getTotalPrice());
        // Step 3: Confirm the order
        assertDoesNotThrow(() -> cart.confirmOrder(), "Order confirmation should succeed");
        assertEquals(stock-quantity,TestItem.fetchStockFromDB(), "Expected Stock should be equal to actual stock");
    }

    @Test
    @Order(4)
    @DisplayName("Client Cancels an order")
    void testClientCancelOrder() {
        order = fm.getOrder("PNscFbGP4pFU2qSfDS3V");
        ArrayList<String> itemsID = order.getItemsID();
        ArrayList<Integer> itemstock= new ArrayList<>();
        assertDoesNotThrow(() -> user.LogIn(testUserID, basePassword), "Login should succeed");
        for (int i = 0; i < itemsID.size(); i++) {
            String itemID = itemsID.get(i);
            if (i > 0 && itemID.equals(itemsID.get(i - 1))) {
                continue;
            }else{
                Item item= fm.getItem(itemID);
                itemstock.add(item.getStock());
            }
        }
        assertDoesNotThrow(() -> user.CancelOrder(order,GlobalData.getCurrentlyLoggedIN()),"Order is removed successfully");
        for (String item1 : (order.getItemsID())) {
            Item item = fm.getItem(item1);
            assertTrue(item.getStock()==itemstock.indexOf(item1)+ order.getItemQuantity(item),"Stock is restored successfully");
        }
    }




}