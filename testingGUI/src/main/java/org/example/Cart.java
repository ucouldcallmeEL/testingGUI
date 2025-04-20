package org.example;

import com.google.cloud.firestore.annotation.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private String CartID;
    private String UserID;
    private List<String> itemsID;
    private String total_price;

    org.example.FireBaseManager fm = FireBaseManager.getInstance();

    public Cart() {}

    public Cart(String UserID){
        this.CartID = UserID;
        this.UserID = UserID;
        this.itemsID = new ArrayList<>();
        this.total_price = "0.0";
    }
    @PropertyName("total_price")
    public String getTotalPrice(){
        return total_price;
    }
    @PropertyName("total_price")
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
        recalculateTotalPrice();
    }

    public void addItem(String userID, Item item, int quantity) throws ZeroStockException {
        if(item.getStock() < quantity){
            throw new ZeroStockException("Item " + item.getItemID() + " has insufficient stock. Available: " + item.getStock() + ", Requested: " + quantity);
        }
        for(int i = 0; i < quantity; i++){
            itemsID.add(item.getItemID());
        }
        //recalculateTotalPrice();

        fm.addItemToCart(userID, item, quantity);
        System.out.println(quantity + " x item " + item.getItemID() + " added to cart.");
    }

    public void removeItem(String userID, Item item, int quantity){
        for(int i = 0; i < quantity; i++ ){
            itemsID.remove(item.getItemID());
        }

        recalculateTotalPrice();
        fm.removeItemFromCart(UserID, item, quantity);
        System.out.println(quantity + " x item " + item.getItemID() + " removed from cart.");
    }

    public int getItemQuantity(Item item){
        if (itemsID.isEmpty()) {
            System.out.println("Cart is empty");
            return 0;
        }
        String itemID = item.getItemID();
        return (int) itemsID.stream().filter(id -> id.equals(itemID)).count();
    }

    public String getItemPrice(Item item){
        int quantity = getItemQuantity(item);
        String itemPrice = item.getItemPrice();
        return String.valueOf(quantity * Double.parseDouble(itemPrice));
    }

    public void confirmOrder() throws ZeroStockException, UpdateException {
        if (itemsID.isEmpty()) {
            System.out.println("Cart is empty. Cannot confirm order");
            return;
        }

        List<String> processed = new ArrayList<>();
        double totalPrice = 0.0;

        for (String itemID : itemsID) {
            if (processed.contains(itemID)) continue;

            Item item = fm.getItem(itemID);
            if (item == null) {
                System.out.println("Item not found in database: " + itemID);
                continue;
            }

            int quantity = (int) itemsID.stream().filter(id -> id.equals(itemID)).count();

            if (item.getStock() < quantity) {
                throw new ZeroStockException("Item " + item.getItemName() + " has insufficient stock. Available: " + item.getStock() + ", Requested: " + quantity);
            }

            try {
                double price = Double.parseDouble(item.getItemPrice().replaceAll("[^\\d.]", ""));
                totalPrice += price * quantity;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format for item " + item.getItemName() + ": " + item.getItemPrice());
            }

            processed.add(itemID);
        }

        Order order = new Order(null, UserID, new ArrayList<>(itemsID), true, String.valueOf(totalPrice));
        fm.addOrder(order);

        processed.clear();
        for (String itemID : itemsID) {
            if (processed.contains(itemID)) continue;

            int quantity = (int) itemsID.stream().filter(id -> id.equals(itemID)).count();
            Item item = fm.getItem(itemID);
            if (item != null) {
                int newStock = item.getStock() - quantity;
                if (newStock < 0) {
                    throw new UpdateException("Calculated negative stock for item: " + itemID);
                }
                item.updateStock(newStock);
            } else {
                System.out.println("Item not found in database during stock update: " + itemID);
            }

            processed.add(itemID);
        }

        fm.emptyCart(UserID);
        itemsID.clear();
        recalculateTotalPrice();

        System.out.println("Order confirmed and stock updated for user: " + UserID);
    }

    

    public void recalculateTotalPrice() {
        double total = 0.0;
        List<String> processed = new ArrayList<>();
        for (String itemID : itemsID) {
            if (processed.contains(itemID)) continue;

            Item item = fm.getItem(itemID);
            if (item != null) {
                int quantity = (int) itemsID.stream().filter(id -> id.equals(itemID)).count();
                try {
                    double price = Double.parseDouble(item.getItemPrice().replaceAll("[^\\d.]", ""));
                    total += price * quantity;
                } catch (NumberFormatException e) {
                    System.out.println("Skipping price for item " + itemID + ": invalid format.");
                }
            }

            processed.add(itemID);
        }

        this.total_price = String.valueOf(total);
    }
}
