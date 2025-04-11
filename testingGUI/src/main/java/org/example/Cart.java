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
        this.total_price = "0.0";
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

    public void addItem(String userID, Item item, int quantity)throws ZeroStockException{
        //add itemID to local list once per quantity
        if(item.getStock() < quantity){
            throw new ZeroStockException("Item " + item.getItemID() + " has insufficient stock. Available: " + item.getStock() + ", Requested: " + quantity);
        }
        for(int i = 0; i < quantity; i++){
            itemsID.add(item.getItemID());
        }
        double currentTotal = Double.parseDouble(total_price);
        double itemPrice = Double.parseDouble(item.getItemPrice());
        currentTotal += itemPrice * quantity;
        total_price = String.valueOf(currentTotal);

        fm.addItemToCart(UserID, item, quantity);
        System.out.println(quantity + " x item " + item.getItemID() + " added to cart.");
    }


    public void removeItem(String userID, Item item, int quantity){
        //remove itemID from list once per quantity
        for(int i = 0; i < quantity; i++ ){
            itemsID.remove(item.getItemID());
        }

        double currentTotal = Double.parseDouble(total_price);
        double itemPrice = Double.parseDouble(item.getItemPrice());
        currentTotal -= itemPrice * quantity;
        total_price = String.valueOf(currentTotal);

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
        Double totalPrice = 0.0;
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
            try {
                String rawPrice = item.getItemPrice().replaceAll("[^\\d.]", ""); // Remove anything that's not digit or dot
                double price = Double.parseDouble(rawPrice);
                totalPrice += price * quantity;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format for item " + itemID + ": " + item.getItemPrice());
                continue;
            }

            processed.add(itemID);
        }


        // If we get here, all items have sufficient stock
        // Step 1: Create a new Order
        org.example.Order order = new Order(null, UserID, new ArrayList<>(itemsID), true,totalPrice.toString());
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
            if (item != null) {
                int currentStock = item.getStock();
                int newStock = currentStock - quantity;

                if (newStock < 0) {
                    throw new UpdateException("Calculated negative stock for item: " + itemID);
                }

                item.updateStock(newStock);
            } else {
                System.out.println("Item not found in database during stock update: " + itemID);
            }

            processed.add(itemID);
        }

        // Step 3: Empty cart in Firebase and locally
        fm.emptyCart(UserID);
        itemsID.clear();

        System.out.println("Order confirmed and stock updated for user: " + UserID);


    }

    public void displayCartItems() throws CartException {
        if (itemsID.isEmpty()) {
            throw new CartException("No cart items found");
        }

        List<String> processed = new ArrayList<>();
        for (String itemID : itemsID) {
            if (processed.contains(itemID)) {
                continue;
            }

            int quantity = (int) itemsID.stream().filter(id -> id.equals(itemID)).count();

            Item item = fm.getItem(itemID);
            if (item != null) {
                System.out.println("Item: " + item.getItemName() +
                        " | ID: " + item.getItemID() +
                        " | Price: " + item.getItemPrice() +
                        " | Quantity: " + quantity);
            } else {
                System.out.println("Item details not found for ID: " + itemID);
            }

            processed.add(itemID);
        }
    }
}
