package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Vendor client = new Vendor("yehia","yehia1","y-ehab@Hotmail.com","y0114487332y","newcairo","01148855498","bloblob");
     org.example.FireBaseManager fm = FireBaseManager.getInstance();
//    // Vendor yehia2 = new Vendor("yehia","yehia2","y-ehab@hotmail.com","12345678","tomato","tomato");
//  //   fm.addVendor(yehia2);
//   Vendor vendor = fm.getVendor("vend");
////        try {
////            vendor.updateStock("Pd9QRE7YMLv5wjTFiVMB",201);
////        } catch (UpdateException e) {
////            throw new RuntimeException(e);
////        }
//       List<String> Items =vendor.getItemsID();
//       for(String item:Items){
//          System.out.println(item);
//       }
//    }

    List<org.example.Order>orders =fm.getHistoryForClient("testClient1");
        for (org.example.Order order : orders) {
            System.out.println(order.getOrderID());
        }
    }}