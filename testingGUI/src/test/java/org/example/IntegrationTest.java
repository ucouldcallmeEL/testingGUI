package org.example;

import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {
    static FireBaseManager fm=FireBaseManager.getInstance();
    private static final String testClientID = "testClient1";
    private static final String baseEmail = "testClient@gmail.com";
    private static final String basePassword = "password123";
    private static final String testVendorID = "testVendor1";

    private static Review review;
    private static Vendor vendor;
    private static Client Client;
    private static Cart cart;
    private static Item ClientItem;
    private static Item vendorItem;
    private static org.example.Order order;

    @BeforeAll
    static void setUp() {
        // Initialize objects or mock dependencies
        Client = fm.getClient(testClientID);
        cart = fm.getClientCart(testClientID);
        ClientItem = fm.getItem("Aq5uyCPe3xhM1ZMFQIrt");
        vendor = fm.getVendor(testVendorID);
        vendorItem=fm.getItem("HCVeQqGzQfI03uqnLZCv");



    }
    @Test
    @Order(1)
    @DisplayName("Register a new client successfully")
    void registerClient_NewUser_Success() {
        String uniqueID = "testClient1";
        try {
            // If the Client already exists (from previous runs), skip registration
            Client.LogIn(uniqueID, basePassword); // this will throw if not registered
        } catch (LogInException e) {
            // User doesn't exist yet → proceed with registration
            assertDoesNotThrow(() ->
                    Client.Register("New User", uniqueID, "new" + baseEmail, basePassword, "123 Street", "01094749270", true)
            );
        }
    }

    @Test
    @Order(2)
    @DisplayName("Register a new Vendor")
    void registerVendorIfNotExists() {
        String VendorID = "testVendor1";

        try {
            vendor.LogIn(VendorID, basePassword);
        } catch (LogInException e) {
            assertDoesNotThrow(() ->vendor.Register("Vendor Name", VendorID, baseEmail, basePassword, "Vendor Street", "01012345678", false));
        }
    }



    @Test
    @Order(3)
    @DisplayName("Login, Add Items to Cart, Confirm Order")
    void testLoginAddToCartAndConfirmOrder() {
        // Step 1: Log in as a Client
        assertDoesNotThrow(() -> Client.LogIn(testClientID, basePassword), "Login should succeed");

        // Step 2: Add ClientItems to the cart
        Item TestItem = ClientItem.getItembyID("Aq5uyCPe3xhM1ZMFQIrt");
        int stock=TestItem.getStock();
        int quantity=1;
        assertDoesNotThrow(() -> cart.addItem(GlobalData.getCurrentlyLoggedIN(), TestItem, quantity), "Item should be added to cart");

        // Assert that the ClientItem was added
        assertTrue(cart.getItemsID().contains("Aq5uyCPe3xhM1ZMFQIrt"), "Cart should contain the added ClientItem");
        assertEquals(cart.getItemPrice(TestItem),"200","price mismatch");
        // Step 3: Confirm the order
        assertDoesNotThrow(() -> cart.confirmOrder(), "Order confirmation should succeed");
        assertEquals(stock-quantity,TestItem.fetchStockFromDB(), "Expected Stock should be equal to actual stock");
    }

    @Test
    @Order(4)
    @DisplayName("add Bulk Order (Stress Test)")
    void testAddItemsToCart() {
        assertDoesNotThrow(() -> Client.LogIn(testClientID, basePassword), "Login should succeed");

        // Step 2: Add ClientItems to the cart


        Item TestItem = ClientItem.getItembyID("Aq5uyCPe3xhM1ZMFQIrt");
        int stock=TestItem.getStock();
        int quantity=800;
        assertDoesNotThrow(() -> cart.addItem(GlobalData.getCurrentlyLoggedIN(), TestItem, quantity), "Item should be added to cart");
        cart=fm.getClientCart(testClientID);
        // Assert that the ClientItem was added
        assertTrue(cart.getItemsID().contains("Aq5uyCPe3xhM1ZMFQIrt"), "Cart should contain the added ClientItem");
        assertEquals(cart.getItemPrice(TestItem),cart.getTotalPrice());
        // Step 3: Confirm the order
        assertDoesNotThrow(() -> cart.confirmOrder(), "Order confirmation should succeed");
        assertEquals(stock-quantity,TestItem.fetchStockFromDB(), "Expected Stock should be equal to actual stock");
    }

    @Test
    @Order(5)
    @DisplayName("Client Cancels an order")
    void testClientCancelOrder() {
        order = fm.getOrder("xNoUGgpNq8fhcOiyfekQ");
        ArrayList<String> ClientItemsID = order.getItemsID();
        HashMap<String, Integer> ClientItemStockMap = new HashMap<>();
        assertDoesNotThrow(() -> Client.LogIn(testClientID, basePassword), "Login should succeed");
        for (String ClientItemID : ClientItemsID) {
            if (!ClientItemStockMap.containsKey(ClientItemID)) { // Only fetch stock for unique ClientItems
                Item ClientItem = fm.getItem(ClientItemID);
                ClientItemStockMap.put(ClientItemID, ClientItem.getStock());
            }
        }
        assertDoesNotThrow(() -> Client.CancelOrder(order,GlobalData.getCurrentlyLoggedIN()),"Order is removed successfully");
        for (String ClientItemID : ClientItemsID) {
            Item ClientItem = fm.getItem(ClientItemID);
            int expectedStock = ClientItemStockMap.get(ClientItemID) + order.getItemQuantity(ClientItem); // Original stock + quantity in order
            assertTrue(ClientItem.getStock() == expectedStock, "Stock is restored successfully for ClientItem: " + ClientItemID);
        }
    }


    @Test
    @Order(6)
    @DisplayName("Vendor Logs In, checks items, adds a new item, and verifies it's listed")
    void testVendorLoginAddNew_Item() {
        // Step 1: Log in as a Vendor
        assertDoesNotThrow(() -> vendor.LogIn(testVendorID, basePassword), "Login should succeed");
        // Step 2: Fetch current vendor items from DB and extract their IDs
        List<Item> vendorItemsBefore = fm.getItemsForVendor(testVendorID);
        ArrayList<String> vendorItemIDsBefore = new ArrayList<>();
        for (Item item : vendorItemsBefore) {
            vendorItemIDsBefore.add(item.getItemID());
        }
        // Step 3: Ensure vendor's internal item list matches DB
        assertEquals(vendorItemIDsBefore, vendor.getItemsID(), "Items match database before addition");
        // Step 4: Add new item (note: vendorItem will still have null ID after this)
        assertDoesNotThrow(() -> vendor.addItem(
                vendorItem.getItemName(),
                vendorItem.getItemDescription(),
                vendorItem.getItemCategory(),
                vendorItem.getItemPrice(),
                vendorItem.getImageURL(),
                vendorItem.getStock(),
                testVendorID
        ), "Item added successfully");
        // Step 5: Re-fetch vendor's items after addition
        List<Item> vendorItemsAfter = fm.getItemsForVendor(testVendorID);
        // Step 6: Search for the newly added item (by name and vendor ID)
        Item addedItem = null;
        for (Item item : vendorItemsAfter) {
            if (item.getItemName().equals(vendorItem.getItemName()) &&
                    item.getVendor().equals(testVendorID)) {
                addedItem = item;
                break;
            }
        }
        // Step 7: Assert item was added and is found
        assertNotNull(addedItem, "Added item should exist in vendor's item list");
        // Step 8: Build updated list of item IDs and check if added item's ID is in the list
        ArrayList<String> vendorItemIDsAfter = new ArrayList<>();
        for (Item item : vendorItemsAfter) {
            vendorItemIDsAfter.add(item.getItemID());
        }
        assertTrue(vendorItemIDsAfter.contains(addedItem.getItemID()), "Added item is in vendor's item list");
        System.out.println("Added item: " + addedItem.getItemID());
    }



    @Test
    @Order(7)
    @DisplayName("Client Logs in, Adds Review on Item, Check item reviews and rating")
    void testClientLoginAddNewReview() {
        assertDoesNotThrow(() -> vendor.LogIn(testClientID, basePassword), "Login should succeed");
        ArrayList<String>Reviews =ClientItem.getReviewsID();
        Client.addReview(ClientItem.getItemID(),5,"amazing");
        ClientItem=fm.getItem(ClientItem.getItemID());
        assertTrue(Reviews.size()<ClientItem.getReviewsID().size(),"Reviews list should increase in size");
        int rating= ClientItem.getRating();
        ClientItem=fm.getItem(ClientItem.getItemID());
        assertEquals(rating,ClientItem.getRating(),"Rating in object should match Database");
        ClientItem.CalculateRating();
        assertEquals(rating,ClientItem.getRating(),"New rating should be correct");

    }


    @Test
    @Order(8)
    @DisplayName("Integration Test of User Class Functions with database")
    void userIntegration_Test() {
        String newEmail = testVendorID+"@gmail.com";
        String newAddress = testVendorID+"@gmail.com";
        String newPassword = testVendorID+"@gmail.com";
        String newPhone ="01148855498";
        // Register user
        registerClient_NewUser_Success();
        // Login user
        assertDoesNotThrow(() -> Client.LogIn(testClientID, basePassword), "Login should succeed");
        //Make search query
        List<Item> results = Client.search("bottle"); // assuming items exist
        assertNotNull(results);
        assertTrue(results.size() >= 0); // even if empty, it shouldn't be null
        // Update email
        assertDoesNotThrow(() -> Client.updateEmail(testClientID, newEmail, basePassword),"change email");
        // Update address
        assertDoesNotThrow(() -> Client.updateAddress(testClientID, newAddress, basePassword),"change address");
        // Update phone number
        assertDoesNotThrow(() -> Client.updatePhoneNumber(testClientID, newPhone, basePassword),"change phone number");
        // Change password
        assertDoesNotThrow(() -> Client.ChangePassword(testClientID, newPassword, basePassword),"change password");
        // Log in with new password
        assertDoesNotThrow(() -> Client.LogIn(testClientID, newPassword));
        // Fetch user and verify updated fields
        Client updatedUser = fm.getClient(testClientID);
        assertNotNull(updatedUser);
        assertEquals(testClientID, updatedUser.getUserID());
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(newAddress, updatedUser.getAddress());
        assertEquals(newPhone, updatedUser.getPhoneNumber());
        // Clean up by resetting password to original (optional)
        assertDoesNotThrow(() -> Client.ChangePassword(testClientID, basePassword, newPassword));
    }

    @Test
    @Order(9    )
    @DisplayName("Test Multi-Vendor Cart Operation and Order Processing")
    void testMultiVendorCartOperation() {
        assertDoesNotThrow(() -> Client.LogIn(testClientID, basePassword), "Login should succeed");
        // Step 2: Add items from different vendors to cart
        Item firstVendorItem = ClientItem; // Using existing item
        Item secondVendorItem = vendorItem; // Using the vendor item from setup
        int firstItemStock = firstVendorItem.getStock();
        int secondItemStock = secondVendorItem.getStock();
        int firstItemQuantity = 2;
        int secondItemQuantity = 3;
        // Add items from different vendors
        assertDoesNotThrow(() -> cart.addItem(GlobalData.getCurrentlyLoggedIN(), firstVendorItem, firstItemQuantity), "First vendor item should be added to cart");
        assertDoesNotThrow(() -> cart.addItem(GlobalData.getCurrentlyLoggedIN(), secondVendorItem, secondItemQuantity), "Second vendor item should be added to cart");
        // Step 3: Verify cart contents
        assertTrue(cart.getItemsID().contains(firstVendorItem.getItemID()), "Cart should contain first vendor item");
        assertTrue(cart.getItemsID().contains(secondVendorItem.getItemID()), "Cart should contain second vendor item");
        // Step 4: Verify total price calculation
        String expectedFirstItemTotal = String.valueOf(Integer.parseInt(firstVendorItem.getItemPrice()) * firstItemQuantity);
        String expectedSecondItemTotal = String.valueOf(Integer.parseInt(secondVendorItem.getItemPrice()) * secondItemQuantity);
        assertEquals(expectedFirstItemTotal, cart.getItemPrice(firstVendorItem), "First item total price should match");
        assertEquals(expectedSecondItemTotal, cart.getItemPrice(secondVendorItem), "Second item total price should match");
        // Step 5: Confirm order with multiple vendors
        assertDoesNotThrow(() -> cart.confirmOrder(), "Order confirmation should succeed with multiple vendors");
        // Step 6: Verify stock updates for both items
        assertEquals(firstItemStock - firstItemQuantity, firstVendorItem.fetchStockFromDB(), "First item stock should be updated correctly");
        assertEquals(secondItemStock - secondItemQuantity, secondVendorItem.fetchStockFromDB(), "Second item stock should be updated correctly");
        // Step 7: Verify order creation in database
        // Get latest order for the client
        List<org.example.Order> clientOrders = fm.getCurrentOrdersForClient(testClientID);
        assertFalse(clientOrders.isEmpty(), "Client should have at least one order");
        // Get most recent order
        org.example.Order latestOrder = clientOrders.get(clientOrders.size() - 1);
        // Verify order contains both items
        assertTrue(latestOrder.getItemsID().contains(firstVendorItem.getItemID()), "Order should contain first vendor item");
        assertTrue(latestOrder.getItemsID().contains(secondVendorItem.getItemID()), "Order should contain second vendor item");
        // Verify quantities in order
        assertEquals(firstItemQuantity, latestOrder.getItemQuantity(firstVendorItem), "First item quantity should match in order");
        assertEquals(secondItemQuantity, latestOrder.getItemQuantity(secondVendorItem), "Second item quantity should match in order");
    }
}