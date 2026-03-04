package org.example.strategy.eligibility;

import org.example.enums.Gender;
import org.example.models.Customer;

public class GenderCriteria implements EligibilityCriteria<Customer> {
    private Gender gender;

    public GenderCriteria(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean isEligible(Customer customer) {
        return customer.getGender() == gender;
    }
}
