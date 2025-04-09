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

    public void confirmOrder() {
        if (itemsID.isEmpty()) {
            System.out.println("Cart is empty. Cannot confirm order");
            return;
        }

        // Step 1: Create a new Order
        org.example.Order order = new Order(null, UserID, new ArrayList<>(itemsID), true);
        fm.addOrder(order);

        // Step 2: Process each unique item
        List<String> processed = new ArrayList<>();
        for (int i = 0; i < itemsID.size(); i++) {
            String itemID = itemsID.get(i);

            // Skip if already processed
            if (processed.contains(itemID)) {
                continue;
            }

            // Count occurrences
            int quantity = 0;
            for (int j = 0; j < itemsID.size(); j++) {
                if (itemsID.get(j).equals(itemID)) {
                    quantity++;
                }
            }

            // Step 3: Get item and its vendor
            Item item = fm.getItem(itemID);
            if (item == null) {
                System.out.println("Item not found in database: " + itemID);
                continue;
            }

            String vendorID = item.getVendor();
            Vendor vendor = fm.getVendor(vendorID);
            if (vendor != null) {
                vendor.updateStock(itemID, quantity, false); // false = reduce stock
            } else {
                System.out.println("Vendor not found for item: " + itemID);
            }

            // Mark this item as processed
            processed.add(itemID);
        }

        // Step 4: Empty cart in Firebase and locally
        fm.emptyCart(UserID);
        itemsID.clear();

        System.out.println("Order confirmed and stock updated for user: " + UserID);
    }


}
