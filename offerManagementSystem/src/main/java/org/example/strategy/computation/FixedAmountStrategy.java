package org.example.strategy.computation;

import org.example.models.Transaction;

public class FixedAmountStrategy implements ComputationStrategy {
    private double amount;

    public FixedAmountStrategy(double amount) {
        this.amount = amount;
    }

    @Override
    public double calculateBenefit(Transaction transaction) {
        return amount;
    }
}
