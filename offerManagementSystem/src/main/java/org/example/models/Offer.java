package org.example.models;

import org.example.strategy.computation.ComputationStrategy;
import org.example.strategy.eligibility.EligibilityCriteria;
import java.util.ArrayList;
import java.util.List;

public class Offer {
    private String id;
    private String name;
    private List<EligibilityCriteria<Customer>> customerEligibilityCriteria;
    private List<EligibilityCriteria<Transaction>> transactionEligibilityCriteria;
    private ComputationStrategy computationStrategy;

    public Offer(String id, String name, ComputationStrategy computationStrategy) {
        this.id = id;
        this.name = name;
        this.computationStrategy = computationStrategy;
        this.customerEligibilityCriteria = new ArrayList<>();
        this.transactionEligibilityCriteria = new ArrayList<>();
    }

    public void addCustomerCriteria(EligibilityCriteria<Customer> criteria) {
        customerEligibilityCriteria.add(criteria);
    }

    public void addTransactionCriteria(EligibilityCriteria<Transaction> criteria) {
        transactionEligibilityCriteria.add(criteria);
    }

    public boolean isEligible(Customer customer, Transaction transaction) {
        for (EligibilityCriteria<Customer> criteria : customerEligibilityCriteria) {
            if (!criteria.isEligible(customer)) return false;
        }
        for (EligibilityCriteria<Transaction> criteria : transactionEligibilityCriteria) {
            if (!criteria.isEligible(transaction)) return false;
        }
        return true;
    }

    public double calculateBenefit(Transaction transaction) {
        return computationStrategy.calculateBenefit(transaction);
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
