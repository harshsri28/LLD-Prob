package org.example.hardware;

import java.util.concurrent.Semaphore;

public class CashDispenser {
    private double availableCash;
    private final Object cashLock = new Object();
    private final Semaphore dispenserSemaphore = new Semaphore(1, true); // Fair semaphore for FCFS

    public CashDispenser(double initialCash) {
        this.availableCash = initialCash;
    }

    public boolean canDispenseCash(double amount) {
        synchronized (cashLock) {
            return availableCash >= amount;
        }
    }

    public boolean dispenseCash(double amount) {
        // Non-blocking attempt - fail fast if dispenser is busy
        if (!dispenserSemaphore.tryAcquire()) {
            System.out.println("[CASH DISPENSER] Dispenser busy, please wait.");
            return false;
        }

        try {
            synchronized (cashLock) {
                if (availableCash >= amount) {
                    availableCash -= amount;
                    System.out.println("[CASH DISPENSER] Dispensed $" + amount +
                            ". Remaining in ATM: $" + availableCash);
                    return true;
                }
                System.out.println("[CASH DISPENSER] Insufficient cash in ATM. Available: $" + availableCash);
                return false;
            }
        } finally {
            dispenserSemaphore.release();
        }
    }

    public void addCash(double amount) {
        synchronized (cashLock) {
            availableCash += amount;
            System.out.println("[CASH DISPENSER] Refilled with $" + amount +
                    ". Total in ATM: $" + availableCash);
        }
    }

    public double getAvailableCash() {
        synchronized (cashLock) {
            return availableCash;
        }
    }
}
