package org.example.strategy.accountStrategy;

public interface AccountStrategy {
    int getAccountNumber();
    double getAvailableBalance();
    double getTotalBalance();
    void deposit(double amount);
    boolean withdraw(double amount);
}
