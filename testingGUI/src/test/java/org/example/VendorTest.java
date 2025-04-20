package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VendorTest {

    private static Vendor vendor;

    @BeforeAll
    static void registerVendorIfNotExists() {
        User user = new User();
        try {
            user.Register("Vendor Name", "vendor123", "vendor@test.com", "securePass", "Vendor Street", "01012345678", false);
        } catch (RegistrationException ignored) {
            // Vendor already exists
        }
        GlobalData.setCurrentlyLoggedIN("vendor123");
        vendor = (Vendor) user.GetUserByID("vendor123");
    }

    @Test
    @Order(1)
    @DisplayName("Constructor initializes vendor with null ItemsID")
    void constructor_InitializesWithNullItemsID() {
        Vendor localVendor = new Vendor("Local Vendor", "localVendor", "local@vendor.com", "securePass", "Street 1", "01098765432");
        assertNull(localVendor.getItemsID(), "ItemsID should be null if not initialized");
    }

    @Test
    @Order(2)
    @DisplayName("Set and get ItemsID")
    void setAndGetItemsID() {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("item123");
        ids.add("item456");

        vendor.setItemsID(ids);
        assertEquals(ids, vendor.getItemsID(), "ItemsID should match what was set");
    }

    @Test
    @Order(3)
    @DisplayName("Add item successfully adds to vendor list")
    void addItem_Success() {
        assertDoesNotThrow(() -> vendor.addItem(
                "Sample Item", "Great item", "Category A", "100.00",
                "C:/Users/amrem/Downloads/Screenshot 2025-04-09 232452.png", 10, "vendor123"
        ));

        assertNotNull(vendor.getItemsID(), "ItemsID list should be initialized");
        assertTrue(vendor.getItemsID().size() > 0, "ItemsID list should contain the new item");
    }

    @Test
    @Order(4)
    @DisplayName("Fail to add item with missing name")
    void addItem_MissingName_ThrowsException() {
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(null, "desc", "cat", "10", "img", 5, "vendor123")
        );
        assertEquals("Please add a name for your item.", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Fail to add item with missing price")
    void addItem_MissingPrice_ThrowsException() {
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem("item", "desc", "cat", "", "img", 5, "vendor123")
        );
        assertEquals("Please add a price for your item.", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Fail to add item with missing category")
    void addItem_MissingCategory_ThrowsException() {
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem("item", "desc", "", "100", "img", 5, "vendor123")
        );
        assertEquals("Please add a category for your item.", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Fail to add item with missing image URL")
    void addItem_MissingImageURL_ThrowsException() {
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem("item", "desc", "cat", "100", "", 5, "vendor123")
        );
        assertEquals("Please add an image for your item.", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Fail to add item with negative stock")
    void addItem_NegativeStock_ThrowsException() {
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem("item", "desc", "cat", "100", "img", -1, "vendor123")
        );
        assertEquals("Please add your item's stock.", exception.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Get items for vendor returns list")
    void getItemsForVendor_ReturnsList() {
        List<Item> items = Vendor.getItemsForVendor("vendor123");
        assertNotNull(items, "Returned item list should not be null");
        assertTrue(items instanceof List, "Returned object should be a List");

        System.out.println("Items returned for vendor123:");
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @Order(10)
    @DisplayName("Remove item from vendor (no error)")
    void removeItem_Success() {
        Item item = new Item();
        item.setItemID("itemToRemove");
        item.setVendor("vendor123");

        assertDoesNotThrow(() -> vendor.removeItem(item));
    }


    @Test
    @Order(11)
    @DisplayName("Remove item from vendor DB (no error)")
    void removeItemFromDB_Success() {
        List<Item> itemsBefore = Vendor.getItemsForVendor("vendor123");
        assertFalse(itemsBefore.isEmpty(), "Vendor must have at least one item to remove");

        Item itemToRemove = itemsBefore.get(itemsBefore.size() - 1); // Remove the most recently added item

        assertDoesNotThrow(() -> vendor.removeItem(itemToRemove), "Removing item should not throw");

        List<Item> itemsAfter = Vendor.getItemsForVendor("vendor123");
        boolean stillExists = itemsAfter.stream()
                .anyMatch(i -> i.getItemID().equals(itemToRemove.getItemID()));

        assertFalse(stillExists, "Item should be removed from vendor's item list");
    }

}
