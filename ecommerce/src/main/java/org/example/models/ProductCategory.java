package org.example.models;

import org.example.enums.ProductCategoryType;

public class ProductCategory {
    private String name;
    private String description;
    private ProductCategoryType type;

    public ProductCategory(String name, String description, ProductCategoryType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProductCategoryType getType() { return type; }
    public void setType(ProductCategoryType type) { this.type = type; }

    @Override
    public String toString() {
        return "ProductCategory{name='" + name + "', type=" + type + "}";
    }
}
