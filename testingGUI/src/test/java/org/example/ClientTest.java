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
    @Order(2)
    @DisplayName("Set and get wishlist")
    void setGetWishlist(){
        List<String> wishlist = List.of("item1", "item2");
        client.setWishlist(wishlist);
        assertEquals(wishlist, client.getWishlist());
    }

    @Test
    @Order(3)
    @DisplayName("Get wishlist from existing DB user")
    void getWishlistFromDB(){

    }




}