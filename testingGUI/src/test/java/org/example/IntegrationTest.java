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
        vendorItem=new Item("amazing laptop","black laptop","Electronics","1200","C:/Users/amrem/Downloads/73894ea1b21b4b73671f8f63de514302573be39d-16x9-x0y152w4000h2250.jpg",9999,testVendorID);



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

        // Validate order details
        // assertEquals("order001", order.getOrderID(), "Order ID should match");
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
        order = fm.getOrder("p4C2uXmO8Ec1pBqz0qco");
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
    @Order(6)
    @DisplayName("Vendor Logs In , check's his Items , add a new Item")
    void testVendorLoginAddNewItem() {
        // Step 1: Log in as a Vendor
        assertDoesNotThrow(() -> vendor.LogIn(testVendorID, basePassword), "Login should succeed");
        // Step 2: Fetch and Check all Items for Sale
        List<Item> VendorItems=fm.getItemsForVendor(testVendorID);
        ArrayList<String> VendorItemsID =new ArrayList<>();
        for (Item VendorItem : VendorItems) {
            VendorItemsID.add(VendorItem.getItemID());
        }
        assertEquals(VendorItemsID, vendor.getItemsID(),"Items Match Database");
        // Step 3: Add a new Item for sale
        assertDoesNotThrow(() -> vendor.addItem(vendorItem.getItemName(),vendorItem.getItemDescription(),vendorItem.getItemCategory(),vendorItem.getItemPrice(),vendorItem.getImageURL(),vendorItem.getStock(),testVendorID),"Item added successfully");
        System.out.println("Expected Item ID: " + vendorItem.getItemID());
        System.out.println("Vendor's Item List: " + vendor.getItemsID());
        assertTrue(vendor.getItemsID().contains(vendorItem.getItemID()),"Item is in Vendor's List");

    }


    //IntegratorTest must not exist in the DB for this test tp pass
    private static User user;
    @Test
    @Order(7)
    @DisplayName("Integration Test of User Class Functions")
    void userIntegration_Test() {


        String name = "Integrator";
        String userID = "IntegratorTest";
        String email = "integrator@gmail.com";
        String password = "integration123";
        String newEmail = "integrator_updated@gmail.com";
        String newAddress = "456 Integration Ave";
        String newPhone = "01012345678";
        String newPassword = "integration456";

        // Register user
        assertDoesNotThrow(() ->
                user.Register(name, userID, email, password, "123 Street", "01094749270", true)
        );

        // Login user
        assertDoesNotThrow(() -> user.LogIn(userID, password));

        //Make search query
        List<Item> results = user.search("bottle"); // assuming items exist
        assertNotNull(results);
        assertTrue(results.size() >= 0); // even if empty, it shouldn't be null

        // Update email
        assertDoesNotThrow(() -> user.updateEmail(userID, newEmail, password));

        // Update address
        assertDoesNotThrow(() -> user.updateAddress(userID, newAddress, password));

        // Update phone number
        assertDoesNotThrow(() -> user.updatePhoneNumber(userID, newPhone, password));

        // Change password
        assertDoesNotThrow(() -> user.ChangePassword(userID, newPassword, password));

        // Log in with new password
        assertDoesNotThrow(() -> user.LogIn(userID, newPassword));

        // Fetch user and verify updated fields
        User updatedUser = user.GetUserByID(userID);
        assertNotNull(updatedUser);
        assertEquals(userID, updatedUser.getUserID());
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(newAddress, updatedUser.getAddress());
        assertEquals(newPhone, updatedUser.getPhoneNumber());

        // Clean up by resetting password to original (optional)
        assertDoesNotThrow(() -> user.ChangePassword(userID, password, newPassword));
    }



}