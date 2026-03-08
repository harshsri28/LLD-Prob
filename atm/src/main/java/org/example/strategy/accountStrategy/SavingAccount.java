package org.example.strategy.accountStrategy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingAccount implements AccountStrategy {
    private int accountNumber;
    private double totalBalance;
    private double availableBalance;
    private double withdrawLimit;
    private final Lock accountLock = new ReentrantLock();

    public SavingAccount(int accountNumber, double initialBalance, double withdrawLimit) {
        this.accountNumber = accountNumber;
        this.totalBalance = initialBalance;
        this.availableBalance = initialBalance;
        this.withdrawLimit = withdrawLimit;
    }

    @Override
    public int getAccountNumber() { return accountNumber; }

    @Override
    public double getAvailableBalance() {
        accountLock.lock();
        try {
            return availableBalance;
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public double getTotalBalance() {
        accountLock.lock();
        try {
            return totalBalance;
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public void deposit(double amount) {
        accountLock.lock();
        try {
            availableBalance += amount;
            totalBalance += amount;
            System.out.println("Deposited $" + amount + " to saving account " + accountNumber +
                    ". New balance: $" + availableBalance);
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public boolean withdraw(double amount) {
        accountLock.lock();
        try {
            if (amount > withdrawLimit) {
                System.out.println("Withdrawal amount $" + amount + " exceeds limit $" + withdrawLimit +
                        " for saving account " + accountNumber);
                return false;
            }
            if (availableBalance >= amount) {
                availableBalance -= amount;
                totalBalance -= amount;
                System.out.println("Withdrew $" + amount + " from saving account " + accountNumber +
                        ". New balance: $" + availableBalance);
                return true;
            }
            System.out.println("Insufficient funds in saving account " + accountNumber +
                    ". Available: $" + availableBalance + ", Requested: $" + amount);
            return false;
        } finally {
            accountLock.unlock();
        }
    }

    public Lock getAccountLock() { return accountLock; }
    public double getWithdrawLimit() { return withdrawLimit; }

    @Override
    public String toString() {
        return "SavingAccount{number=" + accountNumber + ", balance=$" + availableBalance +
                ", limit=$" + withdrawLimit + "}";
    }
}
