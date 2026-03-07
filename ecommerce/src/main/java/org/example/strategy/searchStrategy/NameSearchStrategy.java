package org.example.strategy.searchStrategy;

import org.example.models.Product;
import org.example.repository.ProductRepository;

import java.util.List;

public class NameSearchStrategy implements SearchStrategy {

    @Override
    public List<Product> search(ProductRepository repository, String query) {
        System.out.println("Searching products by name: " + query);
        return repository.searchByName(query);
    }
}
