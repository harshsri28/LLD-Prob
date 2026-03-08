package org.example.transaction;

import org.example.enums.TransactionStatus;
import org.example.strategy.accountStrategy.AccountStrategy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Transfer extends Transaction {
    private AccountStrategy sourceAccount;
    private AccountStrategy destinationAccount;
    private double amount;

    public Transfer(AccountStrategy sourceAccount, AccountStrategy destinationAccount, double amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    @Override
    protected void verifyAccount() {
        if (sourceAccount == null || destinationAccount == null) {
            System.out.println("Account verification failed: source or destination is null.");
            status = TransactionStatus.FAILURE;
            return;
        }
        if (sourceAccount.getAccountNumber() == destinationAccount.getAccountNumber()) {
            System.out.println("Cannot transfer to the same account.");
            status = TransactionStatus.FAILURE;
            return;
        }
        System.out.println("Accounts verified for transfer of $" + amount +
                " from account " + sourceAccount.getAccountNumber() +
                " to account " + destinationAccount.getAccountNumber());
    }

    @Override
    protected void process() {
        // Deadlock prevention: always lock lower account number first
        AccountStrategy first = sourceAccount.getAccountNumber() < destinationAccount.getAccountNumber()
                ? sourceAccount : destinationAccount;
        AccountStrategy second = sourceAccount.getAccountNumber() < destinationAccount.getAccountNumber()
                ? destinationAccount : sourceAccount;

        // Use the account locks in consistent order
        Lock firstLock = getLock(first);
        Lock secondLock = getLock(second);

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                if (sourceAccount.getAvailableBalance() < amount) {
                    System.out.println("Insufficient funds for transfer. Available: $" +
                            sourceAccount.getAvailableBalance() + ", Requested: $" + amount);
                    status = TransactionStatus.FAILURE;
                    return;
                }
                // Perform atomic transfer while both locks are held
                // Bypass the account's own locking since we already hold the locks
                doWithdraw(sourceAccount, amount);
                doDeposit(destinationAccount, amount);
                System.out.println("Transferred $" + amount + " from account " +
                        sourceAccount.getAccountNumber() + " to account " +
                        destinationAccount.getAccountNumber());
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    private Lock getLock(AccountStrategy account) {
        if (account instanceof org.example.strategy.accountStrategy.CheckingAccount) {
            return ((org.example.strategy.accountStrategy.CheckingAccount) account).getAccountLock();
        } else if (account instanceof org.example.strategy.accountStrategy.SavingAccount) {
            return ((org.example.strategy.accountStrategy.SavingAccount) account).getAccountLock();
        }
        // Fallback lock if account type doesn't expose a lock
        return new ReentrantLock();
    }

    // Direct balance manipulation (caller must hold the lock)
    private void doWithdraw(AccountStrategy account, double amount) {
        // Since we already hold the lock, call withdraw which will reacquire (ReentrantLock allows this)
        account.withdraw(amount);
    }

    private void doDeposit(AccountStrategy account, double amount) {
        account.deposit(amount);
    }

    @Override
    protected void updateBalance() {
        if (status != TransactionStatus.FAILURE) {
            System.out.println("Balances updated after transfer.");
        }
    }

    @Override
    protected void generateReceipt() {
        if (status != TransactionStatus.FAILURE) {
            System.out.println("Receipt: Transfer - From Account " + sourceAccount.getAccountNumber() +
                    " To Account " + destinationAccount.getAccountNumber() +
                    ", Amount: $" + amount);
        }
    }
}
