package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.mockito.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class VendorWhiteBoxxingTest {
    private FireBaseManager realFireBaseManager;
    private FireBaseManager spyFireBaseManager;
    private Vendor vendor;

    @BeforeEach
    void setUp() {
        realFireBaseManager = new FireBaseManager();
        spyFireBaseManager = spy(realFireBaseManager);
        Vendor.fm = spyFireBaseManager;
        Mockito.reset(spyFireBaseManager);
        vendor = new Vendor();
        doNothing().when(spyFireBaseManager).deleteItemReviews(anyString());
        doNothing().when(spyFireBaseManager).deleteItem(anyString(), anyString());
    }

    @Test
    @Order(1)
    @DisplayName("Test getter and setter for ItemsID")
    void testGetAndSetItemsID() {
        // Arrange
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        itemsID.add("item2");

        vendor.setItemsID(itemsID);
        assertEquals(itemsID, vendor.getItemsID(), "The ItemsID list should be correctly set and retrieved.");
    }

    @Test
    @Order(2)
    @DisplayName("Add item successfully with valid details")
    void addItem_Success() throws AddItemException {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        String itemID = "item123";

        GlobalData.setCurrentlyLoggedIN(vendorID);

        doReturn(itemID).when(spyFireBaseManager).addItem(any(Item.class));
        assertDoesNotThrow(() -> vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        verify(spyFireBaseManager, times(1)).addItem(any(Item.class));
        assertNotNull(vendor.getItemsID(), "The ItemsID list should not be null.");
        assertTrue(vendor.getItemsID().contains(itemID), "The ItemsID list should contain the added item ID.");
    }

    @Test
    @Order(3)
    @DisplayName("Fail to add item with missing name")
    void addItem_MissingName_Failure() {
        // Arrange
        String itemName = null; // Missing name
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add a name for your item.", exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Fail to add item with missing price")
    void addItem_MissingPrice_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = null; // Missing price
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add a price for your item.", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Fail to add item with missing category")
    void addItem_MissingCategory_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = ""; // Missing category
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add a category for your item.", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Fail to add item with missing image URL")
    void addItem_MissingImageURL_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = ""; // Missing image URL
        int stock = 10;
        String vendorID = "vendor123";
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add an image for your item.", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Fail to add item with negative stock")
    void addItem_NegativeStock_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = -1; // Negative stock
        String vendorID = "vendor123";
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add your item's stock.", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Remove item successfully")
    void removeItem_Success() {
        // Arrange
        String vendorID = "vendor123"; // Ensure the vendor has a valid UserID
        vendor.setUserID(vendorID); // Initialize the UserID for the vendor

        Item mockItem = new Item("Test Item", "Description", "Category",
                "100", "http://example.com/image.jpg", 10, vendorID);
        mockItem.setItemID("item123"); // Ensure the item has a valid itemID
        assertDoesNotThrow(() -> vendor.removeItem(mockItem));
        verify(spyFireBaseManager, times(1)).deleteItemReviews("item123");
        verify(spyFireBaseManager, times(1)).deleteItem("item123", vendorID);
    }

    @Test
    @Order(9)
    @DisplayName("Get items for vendor successfully")
    void getItemsForVendor_Success() {
        // Arrange
        String vendorID = "vendor123";
        List<Item> mockItems = new ArrayList<>();
        mockItems.add(new Item("Item 1", "Description 1", "Category 1",
                "10", "http://example.com/image1.jpg", 10, vendorID));
        mockItems.add(new Item("Item 2", "Description 2", "Category 2",
                "20", "http://example.com/image2.jpg", 5, vendorID));

        doReturn(mockItems).when(spyFireBaseManager).getItemsForVendor(vendorID);
        List<Item> items = Vendor.getItemsForVendor(vendorID);
        assertNotNull(items, "The items list should not be null.");
        assertEquals(2, items.size(), "The items list should contain 2 items.");
        assertEquals(mockItems, items, "The items list should match the mock items.");
    }

    @Test
    @Order(10)
    @DisplayName("Test parameterized constructor with ItemsID")
    void testParameterizedConstructorWithItemsID() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor123";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phoneNumber = "01234567890";
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        itemsID.add("item2");

        Vendor vendor = new Vendor(name, userID, email, password, address, phoneNumber, itemsID);
        assertEquals(name, vendor.getName(), "The name should be correctly set.");
        assertEquals(userID, vendor.getUserID(), "The UserID should be correctly set.");
        assertEquals(email, vendor.getEmail(), "The email should be correctly set.");
        assertEquals(password, vendor.getPassword(), "The password should be correctly set.");
        assertEquals(address, vendor.getAddress(), "The address should be correctly set.");
        assertEquals(phoneNumber, vendor.getPhoneNumber(), "The phone number should be correctly set.");
        assertEquals(itemsID, vendor.getItemsID(), "The ItemsID list should be correctly set.");
    }

    @Test
    @Order(11)
    @DisplayName("Test parameterized constructor without ItemsID")
    void testParameterizedConstructorWithoutItemsID() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor123";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phoneNumber = "01234567890";

        Vendor vendor = new Vendor(name, userID, email, password, address, phoneNumber);
        assertEquals(name, vendor.getName(), "The name should be correctly set.");
        assertEquals(userID, vendor.getUserID(), "The UserID should be correctly set.");
        assertEquals(email, vendor.getEmail(), "The email should be correctly set.");
        assertEquals(password, vendor.getPassword(), "The password should be correctly set.");
        assertEquals(address, vendor.getAddress(), "The address should be correctly set.");
        assertEquals(phoneNumber, vendor.getPhoneNumber(), "The phone number should be correctly set.");
        assertNull(vendor.getItemsID(), "The ItemsID list should be null when not provided.");
    }

    @Test
    @Order(12)
    @DisplayName("Fail to add item with null item ID returned from FireBaseManager")
    void addItem_NullItemID_Failure() throws AddItemException {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Test Description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";

        GlobalData.setCurrentlyLoggedIN(vendorID);
        doReturn(null).when(spyFireBaseManager).addItem(any(Item.class));
        assertDoesNotThrow(() -> vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertNull(vendor.getItemsID(), "The ItemsID list should remain null if itemID is not returned.");
    }

    @Test
    @Order(13)
    @DisplayName("Fail to remove item with null itemID")
    void removeItem_NullItemID_Failure() {
        // Arrange
        Item mockItem = new Item("Test Item", "Description", "Category",
                "100", "http://example.com/image.jpg", 10, "vendor123");
        mockItem.setItemID(null); // Item ID is null
        assertThrows(IllegalArgumentException.class, () -> vendor.removeItem(mockItem));
        verify(spyFireBaseManager, never()).deleteItemReviews(anyString());
        verify(spyFireBaseManager, never()).deleteItem(anyString(), anyString());
    }

    @Test
    @Order(14)
    @DisplayName("Test parameterized constructor with empty ItemsID list")
    void testParameterizedConstructorWithEmptyItemsID() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor123";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phoneNumber = "01234567890";
        ArrayList<String> itemsID = new ArrayList<>(); // Empty list

        Vendor vendor = new Vendor(name, userID, email, password, address, phoneNumber, itemsID);
        assertEquals(itemsID, vendor.getItemsID(), "The ItemsID list should be initialized as an empty list.");
    }

    @Test
    @Order(15)
    @DisplayName("Fail to add item with null category")
    void addItem_NullCategory_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Test Description";
        String itemCategory = null; // Null category
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        GlobalData.setCurrentlyLoggedIN(vendorID);
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add a category for your item.", exception.getMessage());
    }

    @Test
    @Order(16)
    @DisplayName("Fail to add item with null image URL")
    void addItem_NullImageURL_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Test Description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = null; // Null ImageURL
        int stock = 10;
        String vendorID = "vendor123";

        GlobalData.setCurrentlyLoggedIN(vendorID);

        // Act & Assert
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add an image for your item.", exception.getMessage());
    }

    @Test
    @Order(17)
    @DisplayName("Fail to add item with stock less than 0")
    void addItem_InvalidStock_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Test Description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = -1; // Invalid stock
        String vendorID = "vendor123";
        GlobalData.setCurrentlyLoggedIN(vendorID);
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add your item's stock.", exception.getMessage());
    }

    @Test
    @Order(18)
    @DisplayName("Add item with valid stock (0)")
    void addItem_ValidStockZero_Success() throws AddItemException {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Test Description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 0; // Edge case: valid stock = 0
        String vendorID = "vendor123";
        String itemID = "item123";

        GlobalData.setCurrentlyLoggedIN(vendorID);
        doReturn(itemID).when(spyFireBaseManager).addItem(any(Item.class));
        assertDoesNotThrow(() -> vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertNotNull(vendor.getItemsID(), "The ItemsID list should not be null.");
        assertTrue(vendor.getItemsID().contains(itemID), "The ItemsID list should contain the added item ID.");
    }

//    @Test
//    @Order(19)
//    @DisplayName("Remove item with invalid userID")
//    void removeItem_InvalidUserID_Failure() {
//        // Arrange
//        Item mockItem = new Item("Test Item", "Description", "Category", "100", "http://example.com/image.jpg", 10, "vendor123");
//        mockItem.setItemID("item123");
//        vendor.setUserID(null); // Invalid UserID
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> vendor.removeItem(mockItem));
//
//        // Verify that FireBaseManager methods are not called
//        verify(spyFireBaseManager, never()).deleteItemReviews(anyString());
//        verify(spyFireBaseManager, never()).deleteItem(anyString(), anyString());
//    }

    @Test
    @Order(19)
    @DisplayName("Fail to add item with empty category")
    void addItem_EmptyCategory_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Description";
        String itemCategory = ""; // Empty category
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";

        GlobalData.setCurrentlyLoggedIN(vendorID);
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add a category for your item.", exception.getMessage());
    }

    @Test
    @Order(20)
    @DisplayName("Fail to add item with empty image URL")
    void addItem_EmptyImageURL_Failure() {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "Description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = ""; // Empty ImageURL
        int stock = 10;
        String vendorID = "vendor123";

        GlobalData.setCurrentlyLoggedIN(vendorID);
        AddItemException exception = assertThrows(AddItemException.class, () ->
                vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));
        assertEquals("Please add an image for your item.", exception.getMessage());
    }

    @Test
    @Order(21)
    @DisplayName("Test addItem with ItemsID list initialized")
    void addItem_ItemsIDInitialized_Success() throws AddItemException {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        String itemID = "item123";

        // Initialize ItemsID list
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("existingItem");
        vendor.setItemsID(itemsID);

        GlobalData.setCurrentlyLoggedIN(vendorID);
        doReturn(itemID).when(spyFireBaseManager).addItem(any(Item.class));

        // Act
        assertDoesNotThrow(() -> vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));

        // Assert
        assertNotNull(vendor.getItemsID(), "The ItemsID list should not be null.");
        assertTrue(vendor.getItemsID().contains(itemID), "The ItemsID list should contain the added item ID.");
        assertTrue(vendor.getItemsID().contains("existingItem"), "The ItemsID list should retain existing items.");
    }

    @Test
    @Order(22)
    @DisplayName("Test addItem with ItemsID list as null")
    void addItem_ItemsIDNull_Success() throws AddItemException {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";
        String itemID = "item123";

        // Ensure ItemsID is null
        vendor.setItemsID(null);

        GlobalData.setCurrentlyLoggedIN(vendorID);
        doReturn(itemID).when(spyFireBaseManager).addItem(any(Item.class));

        // Act
        assertDoesNotThrow(() -> vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));

        // Assert
        assertNotNull(vendor.getItemsID(), "The ItemsID list should be initialized.");
        assertTrue(vendor.getItemsID().contains(itemID), "The ItemsID list should contain the added item ID.");
    }

//    @Test
//    @Order(23)
//    @DisplayName("Test removeItem with null ItemID")
//    void removeItem_NullItemID_Failure() {
//        // Arrange
//        String vendorID = "vendor123";
//        vendor.setUserID(vendorID);
//
//        // Create an item with a null ItemID
//        Item mockItem = new Item("Test Item", "Description", "Category", "100", "http://example.com/image.jpg", 10, vendorID);
//        mockItem.setItemID(null);
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> vendor.removeItem(mockItem));
//        verify(spyFireBaseManager, never()).deleteItemReviews(anyString());
//        verify(spyFireBaseManager, never()).deleteItem(anyString(), anyString());
//    }

    @Test
    @Order(24)
    @DisplayName("Test removeItem with null UserID")
    void removeItem_NullUserID_Failure() {
        // Arrange
        vendor.setUserID(null); // Null UserID

        // Create a valid item
        Item mockItem = new Item("Test Item", "Description", "Category", "100", "http://example.com/image.jpg", 10, "vendor123");
        mockItem.setItemID("item123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vendor.removeItem(mockItem));
        assertEquals("UserID cannot be null.", exception.getMessage());

        // Verify that FireBaseManager methods are not called
        verify(spyFireBaseManager, never()).deleteItemReviews(anyString());
        verify(spyFireBaseManager, never()).deleteItem(anyString(), anyString());
    }
    @Test
    @Order(25)
    @DisplayName("Test getItemsForVendor with empty list")
    void getItemsForVendor_EmptyList_Success() {
        // Arrange
        String vendorID = "vendor123";

        // Return an empty list
        doReturn(new ArrayList<>()).when(spyFireBaseManager).getItemsForVendor(vendorID);

        // Act
        List<Item> items = Vendor.getItemsForVendor(vendorID);

        // Assert
        assertNotNull(items, "The items list should not be null.");
        assertTrue(items.isEmpty(), "The items list should be empty.");
    }

    @Test
    @Order(26)
    @DisplayName("Test getItemsForVendor with null UserID")
    void getItemsForVendor_NullUserID_Failure() {
        // Arrange
        String vendorID = null; // Null UserID

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Vendor.getItemsForVendor(vendorID));
        verify(spyFireBaseManager, never()).getItemsForVendor(anyString());
    }

    @Test
    @Order(27)
    @DisplayName("Test addItem with fm.addItem returning null")
    void addItem_FireBaseManagerReturnsNull_Success() throws AddItemException {
        // Arrange
        String itemName = "Test Item";
        String itemDescription = "A test item description";
        String itemCategory = "Electronics";
        String itemPrice = "100";
        String imageURL = "http://example.com/image.jpg";
        int stock = 10;
        String vendorID = "vendor123";

        // Ensure fm.addItem returns null
        doReturn(null).when(spyFireBaseManager).addItem(any(Item.class));

        GlobalData.setCurrentlyLoggedIN(vendorID);

        // Act
        assertDoesNotThrow(() -> vendor.addItem(itemName, itemDescription, itemCategory, itemPrice, imageURL, stock, vendorID));

        // Assert
        assertNull(vendor.getItemsID(), "The ItemsID list should remain null if fm.addItem returns null.");
    }
}