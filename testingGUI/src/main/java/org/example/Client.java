package org.example;

import com.google.cloud.firestore.annotation.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private List<String> Wishlist;
    private List<String> History;
    private List<String> CurrentOrders;
    private List<String> reviewHistory;
    private String cart=getUserID();

   FireBaseManager fm = FireBaseManager.getInstance();


    public Client() {}
    public Client(String name, String userID, String email, String password, String address, String phoneNumber,
                  List<String> wishlist, List<String> history, List<String> currentOrders,
                  List<String> reviewHistory, Cart cartID) {
        super(name, userID, email, password, address, phoneNumber);
        this.Wishlist = wishlist;
        this.History = history;
        this.CurrentOrders = currentOrders;
        this.reviewHistory = reviewHistory != null ? reviewHistory : new ArrayList<>();
       Cart cart = new Cart(userID);
       fm.addCart(cart);
    }
    public Client(String name, String userID, String email, String password, String address, String phoneNumber) {
        super(name, userID, email, password, address, phoneNumber);
        this.Wishlist = new ArrayList<>();
        this.History = new ArrayList<>();
        this.CurrentOrders = new ArrayList<>();
        this.reviewHistory = new ArrayList<>();
        Cart cart = new Cart(userID);
        fm.addCart(cart);
    }

    @PropertyName("Wishlist")
    public List<String> getWishlist() {
        return Wishlist;
    }
    @PropertyName("Wishlist")
    public void setWishlist(List<String> Wishlist) {
        this.Wishlist = Wishlist;
    }
    @PropertyName("History")
    public List<String> getHistory() {
        return History;
    }
    @PropertyName("History")
    public void setHistory(List<String> History) {
        this.History = History;
    }
    @PropertyName("CurrentOrders")
    public List<String> getCurrentOrders() {

        return CurrentOrders;
    }
    @PropertyName("CurrentOrders")
    public void setCurrentOrders(List<String> CurrentOrders) {
        this.CurrentOrders = CurrentOrders;
    }
    @PropertyName("reviewHistory")
    public List<String> getReviewHistory() {
        return reviewHistory;
    }
    @PropertyName("reviewHistory")
    public void setReviewHistory(List<String> reviewHistory) {
        this.reviewHistory = reviewHistory;
    }


    public void addItemToWishlist(String itemID){
        if(!this.Wishlist.contains(itemID)){
            this.Wishlist.add(itemID);
            fm.addItemToWishlist(this.getUserID(), itemID);
        }
    }

    public void removeItemFromWishlist(String itemID){
        if(this.Wishlist.contains(itemID)){
            this.Wishlist.remove(itemID);
            fm.removeItemFromWishlist(this.getUserID(), itemID); //updateDB
        } else {

            System.out.println("Item not found in wishlist.");
        }
    }

    public List<String> getWishlistForClientFromDB(){
        return fm.getWishlist(this.getUserID());
    }

    public List<Order> getCurrentOrdersForClientsFromDB(){
        return fm.getCurrentOrdersForClient(this.getUserID());
    }

    public List<Order> getHistoryForClientsFromDB(){
        return fm.getHistoryForClient(this.getUserID());
    }


    public void CancelOrder(Order order, String userID) {
        if(CurrentOrders.remove(order.getOrderID())){
            System.out.println("Order " + order.getOrderID() + " has been cancelled.");
            fm.makeHistory(order.getOrderID(), userID);
        } else {
            System.out.println("Order " + order.getOrderID() + " not found in current orders.");
        }

    }
    public void AddToCart(Item item) {
        fm.addItemToCart(GlobalData.getCurrentlyLoggedIN(),item,1);

    }   
    public void addReview(String itemID, int rating, String comment){

        //create review object
        org.example.Review review = new Review(this.getUserID(), itemID, rating, comment);

        //send review to firebase
        fm.addReview(review);
        System.out.println("Review submitted for item " + itemID);


    }




    public List<Review> getMyReviews(){
        return fm.getReviewsByClient(this.getUserID());
    }


    public List<String> viewCart() {
        Cart cart=fm.getClientCart(this.getUserID());
        return cart.getItemsID();
    }


}

