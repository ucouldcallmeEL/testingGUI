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
        vendorItem=new Item("amazing laptop","black laptop","Electronics","1200","C:/Users/OMEN/Desktop/download.jpeg/",9999,testVendorID);



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
        assertTrue(vendor.getItemsID().contains(vendorItem.getItemID()),"Item is in Vendor's List");

    }


}