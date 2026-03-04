package org.example.strategy.computation;

import org.example.models.Transaction;

public class FixedPercentageStrategy implements ComputationStrategy {
    private double percentage;

    public FixedPercentageStrategy(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateBenefit(Transaction transaction) {
        return (transaction.getAmount() * percentage) / 100.0;
    }
}
