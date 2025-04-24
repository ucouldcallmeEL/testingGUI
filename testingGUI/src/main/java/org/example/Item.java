package org.example;

import com.google.cloud.firestore.annotation.PropertyName;

import java.util.ArrayList;
import java.util.List;


public class Item {

    private String ItemID;
    private String ItemName;
    private String ItemDescription;
    private String ItemCategory;
    private String ItemPrice;
    private String ImageURL;
    private int Stock;
    private String Vendor;
    private int rating;
    private ArrayList<String> ReviewsID;

    public Item() {}
    public Item( String ItemName, String ItemDescription, String ItemCategory, String ItemPrice,String ImageURL, int Stock, String Vendor) {
        this.ItemID = null;
        this.ItemName = ItemName;
        this.ItemDescription = ItemDescription;
        this.ItemCategory = ItemCategory;
        this.ItemPrice = ItemPrice;
        this.ImageURL = ImageURL;
        this.Stock = Stock;
        this.Vendor = Vendor;
        this.rating = 0;
        this.ReviewsID = null;
    }
    static FireBaseManager fm = FireBaseManager.getInstance();


    public Item getItembyID(String ItemId) {
        return fm.getItem(ItemId);
    }
    @PropertyName("ImageURL")
    public String getImageURL() {
        return ImageURL;
    }
    @PropertyName("ImageURL")
    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }

    @PropertyName("rating")
    public void CalculateRating() {
        int sum = 0;
        List<org.example.Review> reviews = fm.getReviewsByItem(this.ItemID);


        //If the list of reviews (reviews) is empty, the reviews.size() will be 0, which results in a java.lang.ArithmeticException: / by zero error.
        if (reviews.isEmpty()) {
            this.rating = 0;
            return;
        }

        for (Review review : reviews) {
            int rating1 = review.getRating();
            sum += rating1;
        }
        int rating2  = sum / reviews.size();
        this.rating = rating2;
        fm.changeItemRating(this.ItemID, rating2);
    }
    public boolean CheckAvailability() {
        if(this.getStock()>0){
            return true;
        }
        else {return false;}
    }
    @PropertyName("ItemName")
    public void changeItemName(String ItemID, String ItemName) throws ChangeException {
        if (ItemName == null || ItemName.trim().isEmpty()) {
            throw new ChangeException("Item name cannot be null or empty.");
        }

        Item item = fm.getItem(ItemID);
        if (item == null) {
            throw new ChangeException("Item not found.");
        }

        item.setItemName(ItemName);
        fm.changeItemName(ItemID, ItemName);
        System.out.println("Item name updated successfully.");
    }
    @PropertyName("ItemDescription")
    public void changeDescription(String ItemID, String Description) throws ChangeException {
        if (Description == null || Description.trim().isEmpty()) {
            throw new ChangeException("Item description cannot be null or empty.");
        }

        Item item = fm.getItem(ItemID);
        if (item == null) {
            throw new ChangeException("Item not found.");
        }

        item.setItemDescription(Description);
        fm.changeItemDescription(ItemID, Description);
        System.out.println("Item description updated successfully.");
    }
    @PropertyName("ItemCategory")
    public void changeCategory(String ItemID, String ItemCategory) throws ChangeException {
        if (ItemCategory == null || ItemCategory.trim().isEmpty()) {
            throw new ChangeException("Item category cannot be null or empty.");
        }

        Item item = fm.getItem(ItemID);
        if (item == null) {
            throw new ChangeException("Item not found.");
        }

        item.setItemCategory(ItemCategory);
        fm.changeItemCategory(ItemID, ItemCategory);
        System.out.println("Item category updated successfully.");
    }
    @PropertyName("ItemCategory")
    public List<Item> getItemsByCategory(String category){
        return fm.getItemsByCategory(category);
    }

    @PropertyName("ItemPrice")
    public void changePrice(String ItemID, String ItemPrice) throws ChangeException {
        if (ItemPrice == null || ItemPrice.trim().isEmpty()) {
            throw new ChangeException("Item price cannot be null or empty.");
        }

        try {
            if (Double.parseDouble(ItemPrice) <= 0) {
                throw new ChangeException("Invalid item price. Price must be greater than zero.");
            }
        } catch (NumberFormatException e) {
            throw new ChangeException("Invalid item price. Price must be a valid number.");
        }

        Item item = fm.getItem(ItemID);
        if (item == null) {
            throw new ChangeException("Item not found.");
        }

        item.setItemPrice(ItemPrice);
        fm.changeItemPrice(ItemID, ItemPrice);
        System.out.println("Item price updated successfully.");
    }

    @PropertyName("Stock")
//    public void updateStock(int newStock) {
//        if(newStock < 0){
//            throw new IllegalArgumentException("Stock cannot be negative");
//        }
//        this.Stock = newStock; // update local object if needed
//        // Assume 'fm' is an instance of FirestoreManager available in scope
//        fm.updateStock(this.ItemID, newStock);
//    }
    public void updateStock (int newStock) throws ChangeException {
        Item item = fm.getItem(ItemID);
        Integer PlaceHolderStock = newStock;
        if (newStock < 0) {
            throw new ChangeException("Stock cannot be negative.");
        }else if(PlaceHolderStock == null){
            throw new ChangeException("Invalid Stock");
        }

        if (item == null) {
            throw new ChangeException("Item not found.");
        }

        item.setStock(newStock);
        fm.updateStock(ItemID, newStock);
        System.out.println("Item stock updated successfully.");
    }

    @PropertyName("Stock")
    public int fetchStockFromDB(){
        return fm.getStock(this.ItemID);
    }


    public String changeImageURL(String ItemID, String ImageURL) throws ChangeException {
        if (ImageURL == null || ImageURL.trim().isEmpty()) {
            throw new ChangeException("Image URL cannot be null or empty.");
        }

        Item item = fm.getItem(ItemID);
        if (item == null) {
            throw new ChangeException("Item not found.");
        }

        item.setImageURL(ImageURL);
        fm.changeItemPicture(ItemID, ImageURL);
        System.out.println("Item image URL updated successfully.");
        return item.getImageURL();
    }



    @PropertyName("ItemID")
    public String getItemID() {
        return ItemID;
    }
    @PropertyName("ItemID")
    public void setItemID(String ItemId) {
        this.ItemID = ItemId;
    }
    @PropertyName("ItemName")
    public String getItemName() {
        return ItemName;
    }
    @PropertyName("ItemName")
    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }
    @PropertyName("ItemDescription")
    public String getItemDescription() {
        return ItemDescription;
    }
    @PropertyName("ItemDescription")
    public void setItemDescription(String ItemDescription) {
        this.ItemDescription = ItemDescription;
    }
    @PropertyName("ItemCategory")
    public String getItemCategory() {
        return ItemCategory;
    }
    @PropertyName("ItemCategory")
    public void setItemCategory(String ItemCategory) {
        this.ItemCategory = ItemCategory;
    }
    @PropertyName("ItemPrice")
    public String getItemPrice() {
        return ItemPrice;
    }
    @PropertyName("ItemPrice")
    public void setItemPrice(String ItemPrice) {
        this.ItemPrice = ItemPrice;
    }
    @PropertyName("Stock")
    public int getStock() {
        return Stock;
    }
    @PropertyName("Stock")
    public void setStock(int Stock) {
        this.Stock = Stock;
    }
    @PropertyName("Vendor")
    public String getVendor() {
        return Vendor;
    }
    @PropertyName("Vendor")
    public void setVendor(String Vendor) {
        this.Vendor = Vendor;
    }
    @PropertyName("rating")
    public int getRating() {
        return rating;
    }
    @PropertyName("rating")
    public void setRating(int rating) {
        this.rating = rating;
    }
    @PropertyName("ReviewsID")
    public ArrayList <String> getReviewsID() {
        return ReviewsID;
    }
    @PropertyName("ReviewsID")
    public void setReviewsID(ArrayList <String> ReviewsID) {
        this.ReviewsID = ReviewsID;
    }
}
class ChangeException extends Exception {
    public ChangeException(String message) {
        super(message);
    }
}

//