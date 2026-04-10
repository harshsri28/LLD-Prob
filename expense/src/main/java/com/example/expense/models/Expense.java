package org.example.models;

import org.example.enums.Category;

public class Expense {
    String id;
    Category category;

    Double money = 0.0;

    String description ="";


    public Expense(String id, Category category, Double money, String description) {
        this.id = id;
        this.category = category;
        this.money = money;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
