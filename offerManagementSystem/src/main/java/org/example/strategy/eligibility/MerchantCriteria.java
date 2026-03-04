package org.example.strategy.eligibility;

import org.example.models.Transaction;

public class MerchantCriteria implements EligibilityCriteria<Transaction> {
    private String merchantName;

    public MerchantCriteria(String merchantName) {
        this.merchantName = merchantName;
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        return transaction.getMerchantName().equalsIgnoreCase(merchantName);
    }
}
