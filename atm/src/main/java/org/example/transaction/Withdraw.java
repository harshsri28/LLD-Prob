package org.example.transaction;

import org.example.enums.TransactionStatus;
import org.example.hardware.CashDispenser;
import org.example.strategy.accountStrategy.AccountStrategy;

public class Withdraw extends Transaction {
    private double amount;
    private AccountStrategy account;
    private CashDispenser cashDispenser;

    public Withdraw(AccountStrategy account, double amount, CashDispenser cashDispenser) {
        this.account = account;
        this.amount = amount;
        this.cashDispenser = cashDispenser;
    }

    @Override
    protected void verifyAccount() {
        if (account == null) {
            System.out.println("Account verification failed: account is null.");
            status = TransactionStatus.FAILURE;
            return;
        }
        if (account.getAvailableBalance() < amount) {
            System.out.println("Insufficient funds. Available: $" + account.getAvailableBalance() +
                    ", Requested: $" + amount);
            status = TransactionStatus.FAILURE;
            return;
        }
        System.out.println("Account " + account.getAccountNumber() + " verified for withdrawal of $" + amount);
    }

    @Override
    protected void process() {
        if (!cashDispenser.canDispenseCash(amount)) {
            System.out.println("ATM does not have enough cash to dispense $" + amount);
            status = TransactionStatus.FAILURE;
            return;
        }

        // Withdraw from account first
        if (!account.withdraw(amount)) {
            System.out.println("Withdrawal from account failed.");
            status = TransactionStatus.FAILURE;
            return;
        }

        // Then dispense cash
        if (!cashDispenser.dispenseCash(amount)) {
            // Rollback - deposit back if cash dispense fails
            account.deposit(amount);
            System.out.println("Cash dispense failed. Amount rolled back to account.");
            status = TransactionStatus.FAILURE;
        }
    }

    @Override
    protected void updateBalance() {
        if (status != TransactionStatus.FAILURE) {
            System.out.println("Balance updated after withdrawal of $" + amount);
        }
    }

    @Override
    protected void generateReceipt() {
        if (status != TransactionStatus.FAILURE) {
            System.out.println("Receipt: Withdrawal - Account " + account.getAccountNumber() +
                    ", Amount: $" + amount + ", Remaining: $" + account.getAvailableBalance());
        }
    }
}
