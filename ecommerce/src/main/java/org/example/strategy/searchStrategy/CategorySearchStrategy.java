package org.example.strategy.searchStrategy;

import org.example.enums.ProductCategoryType;
import org.example.models.Product;
import org.example.repository.ProductRepository;

import java.util.List;

public class CategorySearchStrategy implements SearchStrategy {

    @Override
    public List<Product> search(ProductRepository repository, String query) {
        System.out.println("Searching products by category: " + query);
        ProductCategoryType categoryType = ProductCategoryType.valueOf(query.toUpperCase());
        return repository.searchByCategory(categoryType);
    }
}
