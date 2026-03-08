package org.example.transaction;

import org.example.enums.TransactionStatus;
import org.example.hardware.DepositSlot;
import org.example.strategy.accountStrategy.AccountStrategy;

public class Deposit extends Transaction {
    private double amount;
    private AccountStrategy account;
    private DepositSlot depositSlot;

    public Deposit(AccountStrategy account, double amount, DepositSlot depositSlot) {
        this.account = account;
        this.amount = amount;
        this.depositSlot = depositSlot;
    }

    @Override
    protected void verifyAccount() {
        if (account == null) {
            System.out.println("Account verification failed: account is null.");
            status = TransactionStatus.FAILURE;
            return;
        }
        System.out.println("Account " + account.getAccountNumber() + " verified for deposit of $" + amount);
    }

    @Override
    protected void process() {
        if (!depositSlot.acceptDeposit(amount)) {
            System.out.println("Deposit slot is busy. Please try again.");
            status = TransactionStatus.FAILURE;
            return;
        }
        account.deposit(amount);
    }

    @Override
    protected void updateBalance() {
        if (status != TransactionStatus.FAILURE) {
            System.out.println("Balance updated after deposit of $" + amount);
        }
    }

    @Override
    protected void generateReceipt() {
        if (status != TransactionStatus.FAILURE) {
            System.out.println("Receipt: Deposit - Account " + account.getAccountNumber() +
                    ", Amount: $" + amount + ", New Balance: $" + account.getAvailableBalance());
        }
    }
}
