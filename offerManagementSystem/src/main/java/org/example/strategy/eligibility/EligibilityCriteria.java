package org.example.strategy.eligibility;

public interface EligibilityCriteria<T> {
    boolean isEligible(T item);
}
