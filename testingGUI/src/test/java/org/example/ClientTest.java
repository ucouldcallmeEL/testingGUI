package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.List;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientTest {

    private static Client client;

    @BeforeEach
    void setUp() {
        client = new Client(
                "Test User", "testClient", "test@client.com", "password123", "123 Test St", "01000000000"
        );
    }

    @Test
    @Order(1)
    @DisplayName("Set and get history")
    void setGetHistory() {
        List<String> history = List.of("order1", "order2");
        client.setHistory(history);
        assertEquals(history, client.getHistory(), "History should match the set list");
    }

    @Test
    @Order(2)
    @DisplayName("Set and get current orders")
    void setGetCurrentOrders() {
        List<String> orders = List.of("orderA", "orderB");
        client.setCurrentOrders(orders);
        assertEquals(orders, client.getCurrentOrders(), "Current orders should match the set list");
    }

    @Test
    @Order(3)
    @DisplayName("Set and get review history")
    void setGetReviewHistory() {
        List<String> reviews = List.of("reviewX", "reviewY");
        client.setReviewHistory(reviews);
        assertEquals(reviews, client.getReviewHistory(), "Review history should match the set list");
    }
    @Test
    @Order(4)
    @DisplayName("Constructor initializes empty lists")
    void constructor_InitializesList(){
        assertNotNull(client.getWishlist());
        assertNotNull(client.getHistory());
        assertNotNull(client.getCurrentOrders());
        assertNotNull(client.getReviewHistory());
        assertEquals(0, client.getWishlist().size());
        assertEquals(0, client.getHistory().size());
        assertEquals(0, client.getCurrentOrders().size());
        assertEquals(0, client.getReviewHistory().size());
    }

    @Test
    @Order(5)
    @DisplayName("Set and get wishlist")
    void setGetWishlist(){
        List<String> wishlist = List.of("item1", "item2");
        client.setWishlist(wishlist);
        assertEquals(wishlist, client.getWishlist());
    }




    @Test
    @Order(6)
    @DisplayName("Get wishlist from existing DB user")
    void getWishlistFromDB() {
        User user = new User();
        User retrievedUser = user.GetUserByID("hagar");

        assertNotNull(retrievedUser, "Retrieved user should not be null");
        assertInstanceOf(Client.class, retrievedUser, "User must be a client");

        Client dbClient = (Client) retrievedUser;
        List<String> wishlist = dbClient.getWishlist();

        List<String> expected = List.of("ring001", "wedding ring","borsh");
        assertEquals(expected, wishlist, "Wishlist does not match expected items");
    }

    @Test
    @Order(7)
    @DisplayName("Add item to wishlist and update DB")
    void addItemToWishlist_Success() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");
        String testItemID = "ring002"; // use a real itemID that exists in your Items collection
        assertDoesNotThrow(() -> client.addItemToWishlist(testItemID));
        assertTrue(client.getWishlist().contains(testItemID),
                "Wishlist should contain the newly added item.");

    }

    @Test
    @Order(9)
    @DisplayName("Remove item from wishlist and update DB")
    void removeItemFromWishList_Success(){
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");
        String testItemID = "ring002"; // use a real itemID that exists in your Items collection
        assertDoesNotThrow(() -> client.removeItemFromWishlist(testItemID));
        assertFalse(client.getWishlist().contains(testItemID), "Item should be removed from wishlist");
    }

    @Test
    @Order(10)
    @DisplayName("Cancel existing order moves it to history")
    void cancelOrder_MovesToHistory() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");
        List<String> currentOrders = client.getCurrentOrders();
        assertFalse(currentOrders.isEmpty(), "Client must have at least one current order to test cancellation");
        String orderID = currentOrders.get(0);
        org.example.Order order = new org.example.Order(); //because junit has an order class
        order.setOrderID(orderID);
        assertDoesNotThrow(() -> client.CancelOrder(order, "hagar"));
        assertFalse(client.getCurrentOrders().contains(orderID), "Order should be removed from current orders");
    }


    @Test
    @Order(11)
    @DisplayName("Get history from existing DB user")
    void getHistoryFromDB() {
        User user = new User();
        User retrievedUser = user.GetUserByID("hagar");
        assertNotNull(retrievedUser, "Retrieved user should not be null");
        assertInstanceOf(Client.class, retrievedUser, "User must be a Client");
        Client dbClient = (Client) retrievedUser;
        List<String> history = dbClient.getHistory();
        List<String> expectedHistory = List.of("Yunis", "Othman","Andrea","Wa7ed Shish Tawook");
        assertNotNull(history, "History should not be null");
        assertEquals(expectedHistory.size(), history.size(), "History size should match expected");
        assertTrue(history.containsAll(expectedHistory), "History should contain all expected orders");
    }


    @Test
    @Order(12)
    @DisplayName("Add item to cart successfully")
    void addItemToCart_Success() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");
        Item item = new Item();
        item.setItemID("VOAVMMcKVhzBd6Sx5d0I");
        assertDoesNotThrow(() -> client.AddToCart(item));
        List<String> cartItems = client.viewCart();
        assertTrue(cartItems.contains(item.getItemID()), "Cart should contain the added item");
    }
    @Test
    @Order(13)
    @DisplayName("Submit review for item")
    void addReview_Success() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");
        String testItemID = "VOAVMMcKVhzBd6Sx5d0I"; // this should be a real existing item
        int rating = 4;
        String comment = "Great product!";
        assertDoesNotThrow(() -> client.addReview(testItemID, rating, comment));
        //test will fail if item does not exist in DB
    }

    @Test
    @Order(14)
    @DisplayName("View cart returns items list")
    void viewCart_ReturnsItemList() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");
        List<String> cartItems = client.viewCart();
        assertNotNull(cartItems, "Cart should not be null");
        assertTrue(cartItems instanceof List, "Cart should be a list");
    }

    @Test
    @Order(15)
    @DisplayName("Fetch wishlist from DB using client method")
    void getWishlistForClientFromDB_ReturnsList(){
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client, "Client should not be null");

        List<String> wishlist = client.getWishlistForClientFromDB();
        assertNotNull(wishlist, "Wishlist should not be null");
        assertTrue(wishlist.contains("aRIDVZDogJyN6ks2NXQS"), "Wishlist should contain expected item");

    }

    @Test //run when current orders and history is fixed
    @Order(16)
    @DisplayName("Fetch current orders from DB using client method")
    void getCurrentOrdersForClientsFromDB_ReturnsOrders() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client);

        List<org.example.Order> currentOrders = client.getCurrentOrdersForClientsFromDB();

        assertNotNull(currentOrders);
        assertTrue(currentOrders.size() >= 0, "Current orders list should not be null and should be at least empty");

        if (!currentOrders.isEmpty()) {
            assertTrue(currentOrders.get(0).isCurrent(), "Orders should be marked as current");
        }
    }

    @Test
    @Order(17)
    @DisplayName("Fetch order history from DB using client method")
    void getHistoryForClientsFromDB_ReturnsHistory() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client);

        List<org.example.Order> historyOrders = client.getHistoryForClientsFromDB();

        assertNotNull(historyOrders);
        assertTrue(historyOrders instanceof List);
    }

    @Test
    @Order(18)
    @DisplayName("Retrieve client's submitted reviews")
    void getMyReviews_ReturnsReviewList() {
        User user = new User();
        Client client = (Client) user.GetUserByID("hagar");
        assertNotNull(client);

        List<Review> reviews = client.getMyReviews();

        assertNotNull(reviews);
        assertTrue(reviews instanceof List);
    }








}