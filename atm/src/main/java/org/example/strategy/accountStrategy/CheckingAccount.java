package org.example.strategy.accountStrategy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CheckingAccount implements AccountStrategy {
    private int accountNumber;
    private double totalBalance;
    private double availableBalance;
    private String debitCardNumber;
    private final Lock accountLock = new ReentrantLock();

    public CheckingAccount(int accountNumber, double initialBalance, String debitCardNumber) {
        this.accountNumber = accountNumber;
        this.totalBalance = initialBalance;
        this.availableBalance = initialBalance;
        this.debitCardNumber = debitCardNumber;
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
            System.out.println("Deposited $" + amount + " to checking account " + accountNumber +
                    ". New balance: $" + availableBalance);
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public boolean withdraw(double amount) {
        accountLock.lock();
        try {
            if (availableBalance >= amount) {
                availableBalance -= amount;
                totalBalance -= amount;
                System.out.println("Withdrew $" + amount + " from checking account " + accountNumber +
                        ". New balance: $" + availableBalance);
                return true;
            }
            System.out.println("Insufficient funds in checking account " + accountNumber +
                    ". Available: $" + availableBalance + ", Requested: $" + amount);
            return false;
        } finally {
            accountLock.unlock();
        }
    }

    public Lock getAccountLock() { return accountLock; }
    public String getDebitCardNumber() { return debitCardNumber; }

    @Override
    public String toString() {
        return "CheckingAccount{number=" + accountNumber + ", balance=$" + availableBalance + "}";
    }
}
