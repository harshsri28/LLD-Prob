package org.example.models;

public class Item {
    private String productId;
    private int quantity;
    private double price;

    public Item(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public boolean updateQuantity(int quantity) {
        this.quantity = quantity;
        System.out.println("Quantity updated to: " + quantity);
        return true;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Item{productId='" + productId + "', qty=" + quantity + ", price=" + price + "}";
    }
}
