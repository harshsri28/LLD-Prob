package org.example.strategy;

public class CreditCardStrategy implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card.");
        return true;
    }
}
