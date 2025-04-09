package org.example;

import java.util.ArrayList;
import java.util.List;

public class Vendor extends User {

    private ArrayList<String> ItemsID;

    public Vendor() {
    }

    public Vendor(String name, String UserID, String email, String password, String address, String PhoneNumber, ArrayList<String> ItemsID) {
        super(name, UserID, email, password, address, PhoneNumber);

        this.ItemsID = ItemsID;
    }

    public Vendor(String name, String UserID, String email, String password, String address, String PhoneNumber) {
        super(name, UserID, email, password, address, PhoneNumber);

        this.ItemsID = null;
    }

    public ArrayList<String> getItemsID() {
        return ItemsID;
    }

    public void setItemsID(ArrayList<String> itemsID) {
        ItemsID = itemsID;
    }


    public void addItem(String ItemName, String ItemDescription, String ItemCategory,
                        String ItemPrice, String ImageURL, int Stock, String Vendor)
            throws AddItemException {

        // Validate ItemName
        if(ItemName == null || ItemName.trim().isEmpty()) {
            throw new AddItemException("Please add a name for your item.");
        }

        // Validate ItemPrice
        if(ItemPrice == null || ItemPrice.trim().isEmpty()) {
            throw new AddItemException("Please add a price for your item.");
        }
        try {
            // Try to parse the price to ensure it's a valid number
            Double.parseDouble(ItemPrice);
        } catch (NumberFormatException e) {
            throw new AddItemException("Please enter a valid price for your item.");
        }

        // Validate ItemCategory
        if(ItemCategory == null || ItemCategory.trim().isEmpty()) {
            throw new AddItemException("Please add a category for your item.");
        }

        // Validate ImageURL
        if(ImageURL == null || ImageURL.trim().isEmpty()) {
            throw new AddItemException("Please add an image for your item.");
        }

        // Validate Stock
        if(Stock < 0) {
            throw new AddItemException("Please add your item's stock.");
        }

        // If all validations pass, create and add the item
        Item item = new Item(ItemName, ItemDescription, ItemCategory, ItemPrice,
                ImageURL, Stock, this.getUserID());
        String itemID = fm.addItem(item);

        if(itemID != null) {
            if(this.ItemsID == null) {
                this.ItemsID = new ArrayList<>();
            }
            this.ItemsID.add(itemID);
        }
    }
    public void removeItem(Item Item) {
        fm.deleteItem(Item.getItemID(),this.getUserID());
    }
    public static  List<Item> getItemsForVendor(String userID) {
        List<Item> items = fm.getItemsForVendor(userID);

        return items ;
    }

    public void updateStock(String itemID, int quantityChange, boolean isRestock) {
        // Verify the vendor owns this item
        if (this.ItemsID == null || !this.ItemsID.contains(itemID)) {
            System.out.println("Error: Item not found in vendor's inventory");
            return;
        }

        try {
            // Get current stock from Firebase
            Item currentItem = fm.getItem(itemID);
            if (currentItem == null) {
                System.out.println("Error: Item not found in database");
                return;
            }

            int currentStock = currentItem.getStock();
            int newStock;

            if (isRestock) {
                // Addition for restocking
                newStock = currentStock + quantityChange;
            } else {
                // Subtraction for using up stock
                newStock = currentStock - quantityChange;

                // Prevent negative stock
                if (newStock < 0) {
                    throw new UpdateException("Error: Stock is Negative");
                    //System.out.println("Warning: Cannot reduce stock below 0. Setting to 0.");
                    //newStock = 0;
                }
            }

            // Update stock in Firebase
            fm.updateStock(itemID, newStock);
            System.out.println("Stock updated for item: " + itemID +
                    " | Previous: " + currentStock +
                    " | Change: " + (isRestock ? "+" : "-") + Math.abs(quantityChange) +
                    " | New: " + newStock);

        } catch (UpdateException e) {
            System.out.println("Error updating stock: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
class UpdateException extends Exception {
    public UpdateException(String message) {
        super(message);
    }
}
