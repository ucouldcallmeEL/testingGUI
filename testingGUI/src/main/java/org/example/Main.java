package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Vendor client = new Vendor("yehia","yehia1","y-ehab@Hotmail.com","y0114487332y","newcairo","01148855498","bloblob");
     org.example.FireBaseManager fm = new FireBaseManager();
//        List<Item> items = fm.getItemsForVendor("vend");
//        for(Item item : items){
//            System.out.println(item.getImageURL());
//        }
//        items.getFirst().changeImageURL(items.getFirst().getItemID(),"C:/Users/Malak/Downloads/image.jpeg");
       // Item item = new Item("item4", "test item", "Special Slave", "1010.40", "C:/Users/Malak/Downloads/image2.jpg", 1, "vend");
        //fm.addItem(item);
        //Item item2 = new Item("item5", "test item", "Special Slave", "1010.40", "C:/Users/Malak/Downloads/image.jpeg", 1, "vend");
        //fm.addItem(item);
        List<Item> items=fm.searchBar("w");
        for(Item item:items){
            System.out.println(item.getItemName());
        }
//        User user = new User();
//        user = fm.getUser("vend");
//        System.out.println(user.getName());
    }
}