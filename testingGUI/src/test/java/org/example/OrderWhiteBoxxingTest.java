package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderWhiteBoxxingTest {

    private FireBaseManager mockFireBaseManager;
    private Order order;

    @BeforeEach
    void setUp() {
        mockFireBaseManager = mock(FireBaseManager.class);

        Order.fm = mockFireBaseManager;
        order = new Order();
    }

    @Test
    @DisplayName("Test parameterized constructor")
    void testParameterizedConstructor() {
        // Arrange
        String orderID = "order123";
        String userID = "user123";
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        itemsID.add("item2");
        boolean current = true;
        String totalPrice = "100.50";

        Order testOrder = new Order(orderID, userID, itemsID, current, totalPrice);
        assertEquals(orderID, testOrder.getOrderID());
        assertEquals(userID, testOrder.getUserID());
        assertEquals(itemsID, testOrder.getItemsID());
        assertEquals(current, testOrder.isCurrent());
        assertEquals(totalPrice, testOrder.getTotalPrice());
        assertNotNull(testOrder.getDate());
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        // Act
        Order testOrder = new Order();

        // Assert
        assertNull(testOrder.getOrderID());
        assertNull(testOrder.getUserID());
        assertNull(testOrder.getItemsID());
        assertFalse(testOrder.isCurrent());
        assertNull(testOrder.getTotalPrice());
        assertNull(testOrder.getDate());
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        // Arrange
        String orderID = "order123";
        String userID = "user123";
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        itemsID.add("item2");
        boolean current = true;
        String totalPrice = "100.50";
        Date date = new Date();

        // Act
        order.setOrderID(orderID);
        order.setUserID(userID);
        order.setItemsID(itemsID);
        order.setCurrent(current);
        order.setTotalPrice(totalPrice);
        order.setDate(date);

        // Assert
        assertEquals(orderID, order.getOrderID());
        assertEquals(userID, order.getUserID());
        assertEquals(itemsID, order.getItemsID());
        assertEquals(current, order.isCurrent());
        assertEquals(totalPrice, order.getTotalPrice());
        assertEquals(date, order.getDate());
    }

    @Test
    @DisplayName("Test GetOrderDetails")
    void testGetOrderDetails() {
        // Arrange
        String orderID = "order123";
        String userID = "user123";
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        itemsID.add("item2");
        boolean current = true;

        order.setOrderID(orderID);
        order.setUserID(userID);
        order.setItemsID(itemsID);
        order.setCurrent(current);

        // Act
        String details = order.GetOrderDetails();

        // Assert
        assertTrue(details.contains("Order ID: order123"));
        assertTrue(details.contains("User ID: user123"));
        assertTrue(details.contains("Items ID: [item1, item2]"));
        assertTrue(details.contains("Current: Yes"));
    }

    @Test
    @DisplayName("Test ConfirmOrder")
    void testConfirmOrder() {
        // Arrange
        String orderID = "order123";
        String userID = "user123";

        // Act
        order.ConfirmOrder(orderID, userID);

        // Assert
        verify(mockFireBaseManager, times(1)).makeHistory(orderID, userID);
    }

    @Test
    @DisplayName("Test getItemQuantity with empty itemsID")
    void testGetItemQuantity_EmptyCart() {
        // Arrange
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");
        order.setItemsID(new ArrayList<>()); // Empty list

        // Act
        int quantity = order.getItemQuantity(mockItem);

        // Assert
        assertEquals(0, quantity);
    }

    @Test
    @DisplayName("Test getItemQuantity with populated itemsID")
    void testGetItemQuantity_PopulatedCart() {
        // Arrange
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item123");
        itemsID.add("item123");
        itemsID.add("item456");
        order.setItemsID(itemsID);

        // Act
        int quantity = order.getItemQuantity(mockItem);
        assertEquals(2, quantity);
    }

    @Test
    @DisplayName("Test getItemPrice")
    void testGetItemPrice() {
        // Arrange
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");
        when(mockItem.getItemPrice()).thenReturn("50.0");
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item123");
        itemsID.add("item123");
        order.setItemsID(itemsID);

        String totalPrice = order.getItemPrice(mockItem);
        assertEquals("100.0", totalPrice);
    }

    @Test
    @DisplayName("Test equals method for same object")
    void testEquals_SameObject() {
        // Act & Assert
        assertTrue(order.equals(order));
    }

    @Test
    @DisplayName("Test equals method for null object")
    void testEquals_NullObject() {
        // Act & Assert
        assertFalse(order.equals(null));
    }

    @Test
    @DisplayName("Test equals method for different class")
    void testEquals_DifferentClass() {
        // Arrange
        String otherObject = "NotAnOrder";
        assertFalse(order.equals(otherObject));
    }

    @Test
    @DisplayName("Test equals method for unequal orderID")
    void testEquals_UnequalOrderID() {
        // Arrange
        Order otherOrder = new Order();
        otherOrder.setOrderID("differentID");
        order.setOrderID("order123");
        assertFalse(order.equals(otherOrder));
    }

    @Test
    @DisplayName("Test equals method for unequal itemsID")
    void testEquals_UnequalItemsID() {
        // Arrange
        Order otherOrder = new Order();
        ArrayList<String> itemsID1 = new ArrayList<>();
        itemsID1.add("item1");
        ArrayList<String> itemsID2 = new ArrayList<>();
        itemsID2.add("item2");

        order.setItemsID(itemsID1);
        otherOrder.setItemsID(itemsID2);
        assertFalse(order.equals(otherOrder));
    }

    @Test
    @DisplayName("Test equals method for equal objects")
    void testEquals_EqualObjects() {
        // Arrange
        Order otherOrder = new Order();
        order.setOrderID("order123");
        otherOrder.setOrderID("order123");

        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        order.setItemsID(itemsID);
        otherOrder.setItemsID(itemsID);
        assertTrue(order.equals(otherOrder));
    }

    @Test
    @DisplayName("Test getItemQuantity with null itemsID")
    void testGetItemQuantity_NullItemsID() {
        // Arrange
        order.setItemsID(null);
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");

        // Act
        int quantity = order.getItemQuantity(mockItem);

        // Assert
        assertEquals(0, quantity);
    }

    @Test
    @DisplayName("Test getItemPrice with invalid item price")
    void testGetItemPrice_InvalidPrice() {
        // Arrange
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");
        when(mockItem.getItemPrice()).thenReturn("invalid_price");

        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item123");
        order.setItemsID(itemsID);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> order.getItemPrice(mockItem));
    }

    @Test
    @DisplayName("Test default constructor values")
    void testDefaultConstructorValues() {
        // Assert
        assertNull(order.getOrderID());
        assertNull(order.getUserID());
        assertNull(order.getItemsID());
        assertFalse(order.isCurrent());
        assertNull(order.getTotalPrice());
        assertNull(order.getDate());
    }

    @Test
    @DisplayName("Test equals method with null orderID")
    void testEquals_NullOrderID() {
        // Arrange
        Order otherOrder = new Order();
        otherOrder.setOrderID(null);
        order.setOrderID("order123");

        // Act & Assert
        assertFalse(order.equals(otherOrder));
    }

    @Test
    @DisplayName("Test equals method with null itemsID")
    void testEquals_NullItemsID() {
        // Arrange
        Order otherOrder = new Order();
        otherOrder.setItemsID(null);
        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item1");
        order.setItemsID(itemsID);

        assertFalse(order.equals(otherOrder));
    }

    @Test
    @DisplayName("Test equals method with both null orderID and itemsID")
    void testEquals_BothNull() {
        // Arrange
        Order otherOrder = new Order();
        order.setOrderID(null);
        otherOrder.setOrderID(null);
        order.setItemsID(null);
        otherOrder.setItemsID(null);
        assertTrue(order.equals(otherOrder));
    }

    @Test
    @DisplayName("Test getItemPrice with null price")
    void testGetItemPrice_NullPrice() {
        // Arrange
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");
        when(mockItem.getItemPrice()).thenReturn(null);

        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item123");
        order.setItemsID(itemsID);
        assertThrows(NullPointerException.class, () -> order.getItemPrice(mockItem));
    }

    @Test
    @DisplayName("Test getItemPrice with empty price")
    void testGetItemPrice_EmptyPrice() {
        // Arrange
        Item mockItem = mock(Item.class);
        when(mockItem.getItemID()).thenReturn("item123");
        when(mockItem.getItemPrice()).thenReturn("");

        ArrayList<String> itemsID = new ArrayList<>();
        itemsID.add("item123");
        order.setItemsID(itemsID);
        assertThrows(NumberFormatException.class, () -> order.getItemPrice(mockItem));
    }

    @Test
    @DisplayName("Test GetOrderDetails with null orderID and UserID")
    void testGetOrderDetails_NullValues() {
        // Arrange
        order.setOrderID(null);
        order.setUserID(null);
        order.setItemsID(null);

        String details = order.GetOrderDetails();
        assertTrue(details.contains("Order ID: null"));
        assertTrue(details.contains("User ID: null"));
        assertTrue(details.contains("Items ID: null"));
    }

    @Test
    @DisplayName("Test ConfirmOrder with exception")
    void testConfirmOrder_Exception() {
        // Arrange
        String orderID = "order123";
        String userID = "user123";

        doThrow(new RuntimeException("Mock exception")).when(mockFireBaseManager).makeHistory(orderID, userID);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> order.ConfirmOrder(orderID, userID));
    }
}