package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Catalogue {
    private ArrayList <Item> ItemsID;

    public Catalogue(){}
    public Catalogue(ArrayList <Item> ItemsID){
        this.ItemsID = ItemsID;
    }
    public ArrayList<Item> getItemsID() {
        return ItemsID;
    }
    public void setItemsID(ArrayList<Item> ItemsID) {
        this.ItemsID = ItemsID;
    }

    // Sorting Methods
    public void sortByPriceAscending() {
        Collections.sort(ItemsID, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                double price1 = Double.parseDouble(item1.getItemPrice());
                double price2 = Double.parseDouble(item2.getItemPrice());
                return Double.compare(price1, price2);  // Ascending order
            }
        });
    }
    public void sortByPriceDescending() {
        Collections.sort(ItemsID, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                double price1 = Double.parseDouble(item1.getItemPrice());
                double price2 = Double.parseDouble(item2.getItemPrice());
                return Double.compare(price2, price1);  // Descending order (flipped arguments)
            }
        });
    }

    public void sortByName() {
        Collections.sort(ItemsID, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getItemName().compareToIgnoreCase(item2.getItemName());
            }
        });
    }

    // Filtering Methods
    public ArrayList<Item> filterByCategory(String category) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : ItemsID) {
            if (item.getItemCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<Item> filterByVendor(String provider) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : ItemsID) {
            if (item.getVendor().equalsIgnoreCase(provider)) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<Item> filterByStock(int minStock) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : ItemsID) {
            if (item.getStock() >= minStock) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<Item> filterByRating(double minRating) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : ItemsID) {
            if (item.getRating() >= minRating) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<Item> filterByPrice(double minPrice, double maxPrice) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : ItemsID) {
            double price = Double.parseDouble(item.getItemPrice());
            if (price >= minPrice && price <= maxPrice) {
                result.add(item);
            }
        }
        return result;
    }
}
