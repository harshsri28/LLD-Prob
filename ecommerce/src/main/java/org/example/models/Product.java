package org.example.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private ProductCategory category;
    private AtomicInteger availableItemCount;
    private Account seller;
    private List<Review> reviews;

    public Product(String productId, String name, String description, double price, ProductCategory category, Account seller) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.seller = seller;
        this.availableItemCount = new AtomicInteger(0);
        this.reviews = Collections.synchronizedList(new ArrayList<>());
    }

    public boolean updatePrice(double newPrice) {
        this.price = newPrice;
        System.out.println("Price updated to: " + newPrice);
        return true;
    }

    public boolean decrementStock(int quantity) {
        while (true) {
            int current = availableItemCount.get();
            if (current < quantity) {
                return false;
            }
            if (availableItemCount.compareAndSet(current, current - quantity)) {
                return true;
            }
            // CAS failed because another thread modified stock, retry with fresh value
        }
    }

    public void incrementStock(int quantity) {
        availableItemCount.addAndGet(quantity);
    }

    public void addReview(Review review) { reviews.add(review); }

    public double getAverageRating() {
        synchronized (reviews) {
            if (reviews.isEmpty()) return 0.0;
            return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        }
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public int getAvailableItemCount() { return availableItemCount.get(); }
    public void setAvailableItemCount(int count) { this.availableItemCount.set(count); }
    public Account getSeller() { return seller; }
    public void setSeller(Account seller) { this.seller = seller; }
    public List<Review> getReviews() { return reviews; }

    @Override
    public String toString() {
        return "Product{id='" + productId + "', name='" + name + "', price=" + price +
                ", available=" + availableItemCount.get() + ", category=" + category + "}";
    }
}
