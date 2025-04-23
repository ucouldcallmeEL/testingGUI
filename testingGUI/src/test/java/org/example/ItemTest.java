package org.example;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Order;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item("Laptop", "High performance laptop", "Electronics", "1500", "imageURL", 10, "Vendor001");
        item.setItemID("item001"); // Set a sample ItemID for testing
    }

    @Test
    @Order(1)
    @DisplayName("Constructor initializes all fields correctly")
    void constructor_InitializesFields() {
        assertEquals("Laptop", item.getItemName());
        assertEquals("High performance laptop", item.getItemDescription());
        assertEquals("Electronics", item.getItemCategory());
        assertEquals("1500", item.getItemPrice());
        assertEquals("imageURL", item.getImageURL());
        assertEquals(10, item.getStock());
        assertEquals("Vendor001", item.getVendor());
        assertEquals("item001", item.getItemID());
    }

    @Test
    @Order(2)
    @DisplayName("CheckAvailability returns true for stock > 0")
    void checkAvailability_ReturnsTrue_WhenStockIsPositive() {
        assertTrue(item.CheckAvailability(), "Item should be available when stock > 0");
    }

    @Test
    @Order(3)
    @DisplayName("GetItemByID retrieves item correctly from database")
    void getItemByID_RetrievesItemCorrectly() {
        // Retrieve item by ID from the database
        String itemID = "Aq5uyCPe3xhM1ZMFQIrt";
        Item retrievedItem = item.getItembyID(itemID);

        // Ensure the item is retrieved successfully
        assertNotNull(retrievedItem, "Item should be retrieved from the database");

        // Validate that the retrieved item's ID matches the expected ID
        assertEquals(itemID, retrievedItem.getItemID(), "Retrieved item's ID should match the requested ID");

        // Optionally, print additional details for debugging
        System.out.println("Retrieved Item Details:");
        System.out.println("Item Name: " + retrievedItem.getItemName());
        System.out.println("Item Stock: " + retrievedItem.getStock());
    }

    @Test
    @Order(4)
    @DisplayName("CheckAvailability returns false for stock = 0")
    void checkAvailability_ReturnsFalse_WhenStockIsZero() {
        // Use an existing ItemID from the database
        String itemID = "aRIDVZDogJyN6ks2NXQS"; //item with 0 stock in DB
        // Retrieve the item from the database
        Item retrievedItem = item.getItembyID(itemID);

        // Ensure the item is retrieved successfully
        assertNotNull(retrievedItem, "Item should be retrieved from the database");

        // Update stock to 0
        retrievedItem.updateStock(0);

        // Assert that the item is not available
        assertFalse(retrievedItem.CheckAvailability(), "Item should not be available when stock = 0");
    }
    @Test
    @Order(4)
    @DisplayName("Fetch stock from database returns correct stock")
    void fetchStockFromDB_ReturnsCorrectValue() {
        // Retrieve item by ID from the database
        String itemID = "YqUdM8ilL6U7nrhIFk5b";
        Item retrievedItem = item.getItembyID(itemID);

        // Ensure the item is retrieved successfully
        assertNotNull(retrievedItem, "Item should be retrieved from the database");

        // Fetch stock from the database
        int stock = retrievedItem.fetchStockFromDB();

        // Validate that the stock matches the expected value stored in the database
        assertTrue(stock >= 0, "Stock should be a non-negative value");
        assertEquals(10, stock, "Stock should match the value in the database");
    }

    @Test
    @Order(5)
    @DisplayName("Update stock updates stock in database correctly")
    void updateStock_UpdatesCorrectly() {
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        Item retrievedItem = item.getItembyID(itemID);
        retrievedItem.updateStock(20); // Update stock to a new value
        int updatedStock = retrievedItem.fetchStockFromDB(); // Fetch updated stock from database
        assertEquals(20, updatedStock, "Stock should be updated in the database");
    }

    @Test
    @Order(6)
    @DisplayName("Change item name updates database correctly")
    void changeItemName_UpdatesCorrectly() {
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        Item retrievedItem = item.getItembyID(itemID);
        retrievedItem.changeItemName(itemID, "Cool Laptop"); // Change item name
        Item updatedItem = item.getItembyID(itemID); // Fetch updated item
        assertEquals("Cool Laptop", updatedItem.getItemName());
    }

    @Test
    @Order(7)
    @DisplayName("Change item price updates database correctly")
    void changePrice_UpdatesCorrectly()  throws ChangeException{
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        Item retrievedItem = item.getItembyID(itemID);
        retrievedItem.changePrice(itemID, "2000"); // Change item price
        Item updatedItem = item.getItembyID(itemID); // Fetch updated item
        assertEquals("2000", updatedItem.getItemPrice());
    }

    @Test
    @Order(8)
    @DisplayName("CalculateRating calculates the average rating correctly")
    void calculateRating_CalculatesAverageRating() {
        int rating = item.CalculateRating(); // Calculate average rating
        assertTrue(rating >= 0, "Average rating should be a non-negative integer");
    }

    @Test
    @Order(9)
    @DisplayName("Get items by category retrieves correct items")
    void getItemsByCategory_RetrievesCorrectItems() {
        List<Item> items = item.getItemsByCategory("beauty"); // Fetch items by category
        assertNotNull(items, "Items list should not be null");
        assertTrue(items.size() > 0, "Items list should contain at least one item");
        assertEquals("beauty", items.get(0).getItemCategory());
    }

    @Test
    @Order(10)
    @DisplayName("Change image URL updates database correctly")
    void changeImageURL_UpdatesCorrectly() throws ChangeException {
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        Item retrievedItem = item.getItembyID(itemID);
        String updatedURL = item.changeImageURL(itemID, "C:/Users/amrem/Downloads/photo-1618424181497-157f25b6ddd5.jpg"); // Change image URL
        Item updatedItem = item.getItembyID(itemID); // Fetch updated item
        assertEquals(updatedURL, updatedItem.getImageURL(), "Image URL should match the updated value returned by the function");
    }

    @Test
    @Order(11)
    @DisplayName("Change item description updates database correctly")
    void changeDescription_UpdatesCorrectly() throws ChangeException {
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        Item retrievedItem = item.getItembyID(itemID);
        retrievedItem.changeDescription(itemID, "Shit laptop tbh"); // Change description
        Item updatedItem = item.getItembyID(itemID); // Fetch updated item
        assertEquals("Shit laptop tbh", updatedItem.getItemDescription());
    }

    @Test
    @Order(12)
    @DisplayName("Change item category updates database correctly")
    void changeCategory_UpdatesCorrectly() throws ChangeException {
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        Item retrievedItem = item.getItembyID(itemID);
        retrievedItem.changeCategory(itemID, "Electronics"); // Change category
        Item updatedItem = item.getItembyID(itemID); // Fetch updated item
        assertEquals("Electronics", updatedItem.getItemCategory());
    }

    @Test
    @Order(14)
    @DisplayName("Change item name handles null input gracefully")
    void changeItemName_HandlesNullInput() {
        String itemID = "uiRV3bFyKjRpSlcD69kb";
        assertThrows(IllegalArgumentException.class, () -> item.changeItemName(itemID, null));
    }

    @Test
    @Order(15)
    @DisplayName("Get items by category returns empty list for non-existent category")
    void getItemsByCategory_ReturnsEmptyList_WhenCategoryNotFound() {
        List<Item> items = item.getItemsByCategory("NonExistentCategory");
        assertNotNull(items, "Items list should not be null");
        assertEquals(0, items.size(), "Items list should be empty for a non-existent category");
    }

}