package org.example.repository;

import org.example.enums.ProductCategoryType;
import org.example.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProductRepository {
    private Map<String, Product> products = new ConcurrentHashMap<>();

    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
    }

    public Optional<Product> getProductById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }

    public void removeProduct(String productId) {
        products.remove(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public List<Product> searchByName(String name) {
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> searchByCategory(ProductCategoryType categoryType) {
        return products.values().stream()
                .filter(p -> p.getCategory().getType() == categoryType)
                .collect(Collectors.toList());
    }
}
