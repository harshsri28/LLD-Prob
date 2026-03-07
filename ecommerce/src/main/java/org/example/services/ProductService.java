package org.example.services;

import org.example.models.Account;
import org.example.models.Product;
import org.example.models.ProductCategory;
import org.example.models.Review;
import org.example.repository.ProductRepository;
import org.example.repository.ReviewRepository;
import org.example.strategy.searchStrategy.SearchStrategy;

import java.util.List;
import java.util.Optional;

public class ProductService {
    private ProductRepository productRepo;
    private ReviewRepository reviewRepo;
    private SearchStrategy searchStrategy;

    public ProductService(ProductRepository productRepo, ReviewRepository reviewRepo, SearchStrategy searchStrategy) {
        this.productRepo = productRepo;
        this.reviewRepo = reviewRepo;
        this.searchStrategy = searchStrategy;
    }

    public Product addProduct(String productId, String name, String description, double price,
                              ProductCategory category, Account seller, int stock) {
        Product product = new Product(productId, name, description, price, category, seller);
        product.setAvailableItemCount(stock);
        productRepo.addProduct(product);
        System.out.println("Product added: " + product);
        return product;
    }

    public List<Product> searchProducts(String query) {
        return searchStrategy.search(productRepo, query);
    }

    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }

    public Optional<Product> getProductById(String productId) {
        return productRepo.getProductById(productId);
    }

    public Review addReview(String productId, String memberId, int rating, String comment) {
        Optional<Product> product = productRepo.getProductById(productId);
        if (!product.isPresent()) {
            System.out.println("Product not found: " + productId);
            return null;
        }

        Review review = new Review(productId, memberId, rating, comment);
        product.get().addReview(review);
        reviewRepo.addReview(review);
        System.out.println("Review added for product " + productId + ": " + review);
        return review;
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }
}
