package org.example.strategy.searchStrategy;

import org.example.models.Product;
import org.example.repository.ProductRepository;

import java.util.List;

public interface SearchStrategy {
    List<Product> search(ProductRepository repository, String query);
}
