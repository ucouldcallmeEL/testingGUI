package org.example;

public class cartItem {
    private Item item;
    private int quantity;

    public cartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    // Getters and setters
    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
