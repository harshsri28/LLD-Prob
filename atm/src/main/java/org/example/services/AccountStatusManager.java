package org.example.services;

import org.example.enums.CustomerStatus;

import java.util.concurrent.atomic.AtomicReference;

public class AccountStatusManager {
    private final AtomicReference<CustomerStatus> status =
            new AtomicReference<>(CustomerStatus.ACTIVE);
    private final String accountId;

    public AccountStatusManager(String accountId) {
        this.accountId = accountId;
    }

    public boolean blockAccount() {
        boolean result = status.compareAndSet(CustomerStatus.ACTIVE, CustomerStatus.BLOCKED);
        if (result) {
            System.out.println("Account " + accountId + " blocked.");
        } else {
            System.out.println("Cannot block account " + accountId + ". Current status: " + status.get());
        }
        return result;
    }

    public boolean unblockAccount() {
        boolean result = status.compareAndSet(CustomerStatus.BLOCKED, CustomerStatus.ACTIVE);
        if (result) {
            System.out.println("Account " + accountId + " unblocked.");
        }
        return result;
    }

    public boolean closeAccount() {
        CustomerStatus current = status.get();
        if (current == CustomerStatus.CLOSED) {
            return false;
        }
        return status.compareAndSet(current, CustomerStatus.CLOSED);
    }

    public boolean isActive() {
        return status.get() == CustomerStatus.ACTIVE;
    }

    public CustomerStatus getStatus() {
        return status.get();
    }

    public String getAccountId() { return accountId; }
}
