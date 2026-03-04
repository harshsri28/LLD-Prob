package org.example.strategy.computation;

import org.example.models.Transaction;

public interface ComputationStrategy {
    double calculateBenefit(Transaction transaction);
}
