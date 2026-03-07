package org.example.models;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {
    private List<Item> items;
    private int version; // Optimistic locking

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.version = 0;
    }

    public synchronized boolean addItem(Item item, int currentVersion) {
        if (this.version != currentVersion) {
            throw new ConcurrentModificationException("Cart has been modified by another request.");
        }
        // If item with same productId exists, update quantity
        Optional<Item> existing = items.stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
            System.out.println("Updated quantity for product: " + item.getProductId());
        } else {
            items.add(item);
            System.out.println("Item added to cart: " + item.getProductId());
        }
        this.version++;
        return true;
    }

    public synchronized boolean addItem(Item item) {
        return addItem(item, this.version);
    }

    public synchronized boolean removeItem(String productId, int currentVersion) {
        if (this.version != currentVersion) {
            throw new ConcurrentModificationException("Cart has been modified by another request.");
        }
        boolean removed = items.removeIf(i -> i.getProductId().equals(productId));
        if (removed) {
            this.version++;
            System.out.println("Item removed from cart: " + productId);
        } else {
            System.out.println("Item not found in cart: " + productId);
        }
        return removed;
    }

    public synchronized boolean removeItem(String productId) {
        return removeItem(productId, this.version);
    }

    public synchronized boolean updateItemQuantity(String productId, int newQuantity) {
        Optional<Item> item = items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
        if (item.isPresent()) {
            if (newQuantity <= 0) {
                return removeItem(productId);
            }
            item.get().setQuantity(newQuantity);
            this.version++;
            System.out.println("Cart item " + productId + " quantity updated to " + newQuantity);
            return true;
        }
        System.out.println("Item not found in cart: " + productId);
        return false;
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(Item::getTotalPrice).sum();
    }

    public synchronized boolean checkout() {
        if (items.isEmpty()) {
            System.out.println("Cart is empty. Nothing to checkout.");
            return false;
        }
        System.out.println("Checkout completed. Total: $" + getTotalAmount());
        return true;
    }

    public synchronized void clear() {
        items.clear();
        this.version++;
    }

    public List<Item> getItems() { return new ArrayList<>(items); }
    public int getVersion() { return version; }

    @Override
    public String toString() {
        return "ShoppingCart{items=" + items + ", total=$" + getTotalAmount() + "}";
    }
}
