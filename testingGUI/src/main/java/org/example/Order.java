package org.example;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private String orderID;
    private String UserID;
    private ArrayList <String> itemsID;
    private boolean current;
    private String TotalPrice;
    private Date Date;


    public Order(){}

    public Order(String orderID, String UserID, ArrayList<String> itemsID, boolean current){
        this.orderID = orderID;
        this.UserID = UserID;
        this.itemsID = itemsID;
        this.current = current;
        this.Date = new Date();
    }
    public void setDate(Date Date){
        this.Date = Date;
    }

    public Date getDate() {
        return Date;
    }

    public String getTotalPrice(){
        return TotalPrice;
    }
    public void setTotalPrice(String totalPrice){
        this.TotalPrice = totalPrice;
    }
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public ArrayList<String> getItemsID() {
        return itemsID;
    }

    public void setItemsID(ArrayList<String> itemsID) {
        this.itemsID = itemsID;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

//
    static FireBaseManager fm = FireBaseManager.getInstance();

    public String GetOrderDetails() {
        return "Order ID: " + orderID + "\n" +
                "User ID: " + UserID + "\n" +
                "Items ID: " + itemsID.toString() + "\n" +
                "Current: " + (current? "Yes" : "No");
    }
    public void ConfirmOrder(String OrderID, String UserID){
        fm.makeHistory(OrderID, UserID);
    }
    //this shit is for testing purposes only, take it to a separate compiler or project or whatever,
    // until we start performing junit testing
//    public static void main(String[] args) {
//        ArrayList<Integer> itemsList = new ArrayList<>();
//        itemsList.add(1001);
//        itemsList.add(1002);
//        itemsList.add(1003);
//
//        //Order order = new Order("101", "user123", "itemsList", true);
//
//
//       // System.out.println(order.GetOrderDetails());
//    }

}
