package org.example.transaction;

import org.example.enums.TransactionStatus;
import org.example.strategy.accountStrategy.AccountStrategy;

public class BalanceInquiry extends Transaction {
    private AccountStrategy account;

    public BalanceInquiry(AccountStrategy account) {
        this.account = account;
    }

    @Override
    protected void verifyAccount() {
        if (account == null) {
            System.out.println("Account verification failed: account is null.");
            status = TransactionStatus.FAILURE;
            return;
        }
        System.out.println("Account " + account.getAccountNumber() + " verified for balance inquiry.");
    }

    @Override
    protected void process() {
        double balance = account.getAvailableBalance();
        System.out.println("Current balance for account " + account.getAccountNumber() + ": $" + balance);
    }

    @Override
    protected void updateBalance() {
        // No balance update needed for balance inquiry
    }

    @Override
    protected void generateReceipt() {
        System.out.println("Receipt: Balance Inquiry - Account " + account.getAccountNumber() +
                ", Balance: $" + account.getAvailableBalance());
    }
}
