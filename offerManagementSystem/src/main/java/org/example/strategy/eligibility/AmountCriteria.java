package org.example.strategy.eligibility;

import org.example.models.Transaction;

public class AmountCriteria implements EligibilityCriteria<Transaction> {
    private double minAmount;

    public AmountCriteria(double minAmount) {
        this.minAmount = minAmount;
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        return transaction.getAmount() >= minAmount;
    }
}
