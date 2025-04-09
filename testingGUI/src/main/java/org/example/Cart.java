package org.example;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private String CartID;
    private String UserID;
    private List<String> itemsID;
    private String total_price;

    org.example.FireBaseManager fm = new FireBaseManager();


    public Cart(){}

    public Cart(String UserID){
        this.CartID = UserID;
        this.UserID = UserID;
        this.itemsID = new ArrayList<>();
        this.total_price = null;
    }

    public String getTotalPrice(){
        return total_price;
    }

    public void setTotalPrice(String total_price){
        this.total_price = total_price;
    }

    public String getCartID() {
        return CartID;
    }

    public void setCartID(String cartID) {
        CartID = cartID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public List<String> getItemsID() {
        return itemsID;
    }
    public void setItemsID(List<String> itemsID) {
        this.itemsID = itemsID;
    }

    public void addItem(String userID, Item item, int quantity){
        //add itemID to local list once per quantity
        for(int i = 0; i < quantity; i++){
            itemsID.add(item.getItemID());
        }
        fm.addItemToCart(UserID, item, quantity);
        System.out.println(quantity + " x item " + item.getItemID() + " added to cart.");
    }

    public void removeItem(String userID, Item item, int quantity){
        //remove itemID from list once per quantity
        for(int i = 0; i < quantity; i++ ){
            itemsID.remove(item.getItemID());
        }

        fm.removeItemFromCart(UserID, item, quantity);
        System.out.println(quantity + " x item " + item.getItemID() + " removed to cart.");

    }

    public void confirmOrder() throws ZeroStockException, UpdateException {
        if (itemsID.isEmpty()) {
            System.out.println("Cart is empty. Cannot confirm order");
            return;
        }

        // First check all items for stock availability
        List<String> processed = new ArrayList<>();
        for (String itemID : itemsID) {
            if (processed.contains(itemID)) {
                continue;
            }

            Item item = fm.getItem(itemID);
            if (item == null) {
                System.out.println("Item not found in database: " + itemID);
                continue;
            }

            // Count occurrences of this item in cart
            int quantity = (int) itemsID.stream().filter(id -> id.equals(itemID)).count();

            if (item.getStock() < quantity) {
                throw new ZeroStockException("Item " + itemID + " has insufficient stock. Available: " + item.getStock() + ", Requested: " + quantity);
            }

            processed.add(itemID);
        }

        // If we get here, all items have sufficient stock
        // Step 1: Create a new Order
        org.example.Order order = new Order(null, UserID, new ArrayList<>(itemsID), true);
        fm.addOrder(order);

        // Step 2: Process each unique item to update stock
        processed.clear();
        for (String itemID : itemsID) {
            if (processed.contains(itemID)) {
                continue;
            }

            // Count occurrences
            int quantity = (int) itemsID.stream().filter(id -> id.equals(itemID)).count();

            Item item = fm.getItem(itemID);
            String vendorID = item.getVendor();
            Vendor vendor = fm.getVendor(vendorID);
            if (vendor != null) {
                int stock =  fm.getStock(itemID);
                stock = stock - quantity;
                vendor.updateStock(itemID, stock); // false = reduce stock
            } else {
                System.out.println("Vendor not found for item: " + itemID);
            }

            processed.add(itemID);
        }

        // Step 3: Empty cart in Firebase and locally
        fm.emptyCart(UserID);
        itemsID.clear();

        System.out.println("Order confirmed and stock updated for user: " + UserID);
    }
}
