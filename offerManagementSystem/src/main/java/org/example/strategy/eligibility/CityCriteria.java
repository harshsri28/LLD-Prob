package org.example.strategy.eligibility;

import org.example.models.Transaction;

public class CityCriteria implements EligibilityCriteria<Transaction> {
    private String city;

    public CityCriteria(String city) {
        this.city = city;
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        return transaction.getCity().equalsIgnoreCase(city);
    }
}
