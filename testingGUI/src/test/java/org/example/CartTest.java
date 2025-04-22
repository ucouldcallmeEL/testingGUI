package org.example;
import org.junit.jupiter.api.*;
import java.util.*;
import org.junit.jupiter.api.Order;


import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

//for commit

class CartTest {
    private Cart TestCart=FireBaseManager.getInstance().getClientCart("hagar");
    private Item sampleItem=new Item();

    //commit test

    @BeforeEach
    void setup(){
        Client client = new Client(
                "Test User", "testClient1", "test@client.com", "password123", "123 Test St", "01000000000"

        );
    }
    @Test
    @Order(1)
    @DisplayName("Set and get current Total Price")
    void setGetTotalPrice(){
        String newPrice ="5151";
        TestCart.setTotalPrice(newPrice);
        assertEquals("5151",TestCart.getTotalPrice());
    }
    @Test
    @Order(2)
    @DisplayName("Set and get CartID")
    void setGetCartID(){
        String newID ="Sheblanga";
        TestCart.setCartID(newID);
        assertEquals("Sheblanga",TestCart.getCartID());
    }
    @Test
    @Order(3)
    @DisplayName("Set and get UserID")
    void setGetUserID(){
        String newID="MIGHTYDREW";
        TestCart.setUserID(newID);
        assertEquals("MIGHTYDREW",TestCart.getUserID());
    }
    @Test
    @Order(4)
    @DisplayName("Set and get ItemsID")
    void setGetItemsID(){
        List<String> ItemsID = new ArrayList<String>(Arrays.asList("Item123","Items321"));
        TestCart.setItemsID(ItemsID);
        assertEquals(ItemsID,TestCart.getItemsID());
    }

    @Test
    @Order(5)
    @DisplayName("Add item to Cart")
    void addItem(){
        Item Item=sampleItem.getItembyID("Aq5uyCPe3xhM1ZMFQIrt");
        assertDoesNotThrow(() ->TestCart.addItem("hagar",Item,1), "Item should have stock");
        assertTrue(TestCart.getItemsID().contains("Aq5uyCPe3xhM1ZMFQIrt"));
    }

    @Test
    @Order(6)
    @DisplayName("Remove item from Cart")
    void removeItem(){
        Item Item=sampleItem.getItembyID("Aq5uyCPe3xhM1ZMFQIrt");
        TestCart.removeItem("hagar",Item,1);
        assertFalse(TestCart.getItemsID().contains("Aq5uyCPe3xhM1ZMFQIrt"));
    }
    @Test
    @Order(7)
    @DisplayName("Get Item Quantity")
    void getQuantity(){
        Item Item=sampleItem.getItembyID("VOAVMMcKVhzBd6Sx5d0I");
        int x=TestCart.getItemQuantity(Item);
        assertEquals(1,x);
    }
    @Test
    @Order(8)
    @DisplayName("Get Item TotalPrice")
    void getItemPrice(){
        Item item=sampleItem.getItembyID("VOAVMMcKVhzBd6Sx5d0I");
        String x=TestCart.getItemPrice(item);
        assertEquals("1050.0",x);
    }
    @Test
    @Order(9)
    @DisplayName("Recalculate Total Price")
    void recalculate(){
        TestCart.recalculateTotalPrice();
        assertEquals("1050.0",TestCart.getTotalPrice());
    }
    @Test
    @Order(10)
    @DisplayName("Confirm Order")
    void confirmOrder(){
        assertDoesNotThrow(() -> TestCart.confirmOrder(),"Item must have Stock");
        assertTrue(TestCart.getItemsID().isEmpty());
    }



}
