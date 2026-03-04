package org.example.strategy.computation;

import org.example.models.Transaction;

public class CappedPercentageStrategy implements ComputationStrategy {
    private double percentage;
    private double maxCap;

    public CappedPercentageStrategy(double percentage, double maxCap) {
        this.percentage = percentage;
        this.maxCap = maxCap;
    }

    @Override
    public double calculateBenefit(Transaction transaction) {
        double benefit = (transaction.getAmount() * percentage) / 100.0;
        return Math.min(benefit, maxCap);
    }
}
