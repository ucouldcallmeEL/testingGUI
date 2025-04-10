package org.example;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private List<String> Wishlist;
    private List<String> History;
    private List<String> CurrentOrders;
    private List<String> reviewHistory;
    private String cart=getUserID();

    FireBaseManager fm = new FireBaseManager();
    //static FireBaseManager fm = FireBaseManager.getInstance();


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

    public List<String> getWishlist() {
        return Wishlist;
    }

    public void setWishlist(List<String> wishlist) {
        Wishlist = wishlist;
    }

    public List<String> getHistory() {
        return History;
    }

    public void setHistory(List<String> history) {
        History = history;
    }

    public List<String> getCurrentOrders() {

        return CurrentOrders;
    }

    public void setCurrentOrders(List<String> currentOrders) {
        CurrentOrders = currentOrders;
    }

    public List<String> getReviewHistory() {
        return reviewHistory;
    }

    public void setReviewHistory(List<String> reviewHistory) {
        this.reviewHistory = reviewHistory;
    }


    public void addItemToWishList(String itemID){
        if(!this.Wishlist.contains(itemID)){
            this.Wishlist.add(itemID);
            fm.addItemToWishlist(this.getUserID(), itemID);
        }
    }




    public void CancelOrder(Order order, String userID) {
        if(CurrentOrders.remove(order.getOrderID())){
            System.out.println("Order " + order.getOrderID() + " has been cancelled.");
            fm.makeHistory(order.getOrderID(), userID);
        } else {
            System.out.println("Order " + order.getOrderID() + " not found in current orders.");
        }

    }
    public String BrowseItems(Item item){
        return item.getItemID();
        //UI Function NOT FINAL
    }

    public void AddToCart(){

    }
    public void addReview(String itemID, int rating, String comment){

        //create review object
        org.example.Review review = new Review(this.getUserID(), itemID, rating, comment);

        //send review to firebase
        fm.addReview(review);
        System.out.println("Review submitted for item " + itemID);


    }


    public List<String> viewCart() {
        Cart cart=fm.getClientCart(this.getUserID());
        return cart.getItemsID();
    }


}

