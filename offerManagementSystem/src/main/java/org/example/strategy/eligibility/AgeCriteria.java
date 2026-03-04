package org.example.strategy.eligibility;

import org.example.models.Customer;

public class AgeCriteria implements EligibilityCriteria<Customer> {
    private int minAge;
    
    public AgeCriteria(int minAge) {
        this.minAge = minAge;
    }

    @Override
    public boolean isEligible(Customer customer) {
        return customer.getAge() >= minAge;
    }
}
