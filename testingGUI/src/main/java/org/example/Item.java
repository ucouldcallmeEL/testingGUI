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
        return fm.getItem(ItemID);
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }

    public int CalculateRating() {
        int sum = 0;
        List<org.example.Review> reviews =fm.getReviewsByItem(this.ItemID);
        for(Review review : reviews) {
            int rating = review.getRating();
            sum += rating;
        }
        rating = sum / reviews.size();
    return rating;
    }

    public boolean CheckAvailability() {
        if(this.getStock()>0){
            return true;
        }
        else {return false;}
    }
    public void changeItemName(String ItemID,String ItemName) {
        Item item=fm.getItem(ItemID);
        item.setItemName(ItemName);
        fm.changeItemName(ItemID,ItemName);
    }
    public void changeDescription(String ItemID,String Description) {
        Item item=fm.getItem(ItemID);
        item.setItemDescription(Description);
        fm.changeItemDescription(ItemID,Description);
    }
    public void changeCategory(String ItemID,String ItemCategory) {
        Item item=fm.getItem(ItemID);
        item.setItemCategory(ItemCategory);
        fm.changeItemCategory(ItemID,ItemCategory);
    }
    public void changePrice(String ItemID,String ItemPrice) {
        Item item=fm.getItem(ItemID);
        item.setItemPrice(ItemPrice);
        fm.changeItemPrice(ItemID,ItemPrice);
    }


    public void updateStock(int newStock) {
        this.Stock = newStock; // update local object if needed
        // Assume 'fm' is an instance of FirestoreManager available in scope
        fm.updateStock(this.ItemID, newStock);
    }

    public void changeImageURL(String ItemID,String ImageURL) {
        fm.changeItemPicture(ItemID,ImageURL);
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
//