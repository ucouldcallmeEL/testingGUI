package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Vendor client = new Vendor("yehia","yehia1","y-ehab@Hotmail.com","y0114487332y","newcairo","01148855498","bloblob");
     org.example.FireBaseManager fm = new FireBaseManager();
    Vendor vendor = fm.getVendor("vend");
        try {
            vendor.addItem("whatever3","whatever1","whatever2","300","C:/Users/Malak/Downloads/image.jpeg",300,"vend");
        } catch (AddItemException e) {
            throw new RuntimeException(e);
        }
    }
}