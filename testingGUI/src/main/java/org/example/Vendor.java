package org.example;

import com.google.cloud.firestore.annotation.PropertyName;

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
    @PropertyName("ItemsID")
    public ArrayList<String> getItemsID() {
        return ItemsID;
    }
    @PropertyName("ItemsID")
    public void setItemsID(ArrayList<String> ItemsID) {
        this.ItemsID = ItemsID;
    }
//

    public void addItem( String ItemName, String ItemDescription, String ItemCategory, String ItemPrice,String ImageURL, int Stock, String Vendor) throws AddItemException {
        //Item item = new Item(ItemName, ItemDescription, ItemCategory, ItemPrice,ImageURL, Stock, this.getUserID());
        //String itemID = fm.addItem(item);

        boolean ValidName = false;
        boolean ValidPrice = false;
        boolean ValidCategory = false;
        boolean ValidImageURL = false;
        boolean ValidStock = false;

        if(!ValidName){
            if(ItemName == null){
                throw new AddItemException("Please add a name for your item.");
            }else{
                ValidName = true;
            }
        }
        if(!ValidPrice){
            if(ItemPrice == null || ItemPrice.length() <= 0){
                throw new AddItemException("Please add a price for your item.");
            }else{
                ValidPrice = true;
            }
        }
        if(!ValidCategory){
            if(ItemCategory == null || ItemCategory.length() == 0){
                throw new AddItemException("Please add a category for your item.");
            }else{
                ValidCategory = true;
            }
        }
        if(!ValidImageURL){
            if(ImageURL == null || ImageURL.length() == 0){
                throw new AddItemException("Please add an image for your item.");
            }else{
                ValidImageURL = true;
            }
        }
        if(!ValidStock){
            if(Stock >= 0){
                ValidStock = true;
            }else{
                throw new AddItemException("Please add your item's stock.");
            }
        }

        Item item = new Item(ItemName, ItemDescription, ItemCategory, ItemPrice,ImageURL, Stock, GlobalData.getCurrentlyLoggedIN());
        String itemID = fm.addItem(item);

        if(itemID != null){
            if(this.ItemsID == null){
                this.ItemsID = new ArrayList<>();
            }
            this.ItemsID.add(itemID);
        }

        //String itemID = fm.addItem(item);

    }
//    public void UpdateItem(String ItemID, String newName,  )
    public void removeItem(Item Item) {
        fm.deleteItemReviews(Item.getItemID());
        fm.deleteItem(Item.getItemID(),this.getUserID());
    }
    public static  List<Item> getItemsForVendor(String userID) {
        List<Item> items = fm.getItemsForVendor(userID);
        return items ;
    }

}
class UpdateException extends Exception {
    public UpdateException(String message) {
        super(message);
    }
}
