package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    String id;
    List<Expense> expenses;

    public User(String id, List<Expense> expenses) {
        this.id = id;
        this.expenses = new ArrayList<>();
    }

    void addExpense(Expense expense){
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }
}
