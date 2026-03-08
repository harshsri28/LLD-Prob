package org.example.transaction;

import org.example.enums.TransactionStatus;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Transaction {
    // AtomicInteger for thread-safe transaction ID generation
    private static final AtomicInteger idCounter = new AtomicInteger(1000);

    protected int transactionId;
    protected Date creationTime;
    protected TransactionStatus status;

    public Transaction() {
        this.transactionId = idCounter.getAndIncrement();
        this.creationTime = new Date();
        this.status = TransactionStatus.NONE;
    }

    // Template Method - defines the steps for a transaction
    public final void executeTransaction() {
        verifyAccount();
        if (status == TransactionStatus.FAILURE) {
            return;
        }
        process();
        if (status == TransactionStatus.FAILURE) {
            return;
        }
        updateBalance();
        generateReceipt();
        if (status != TransactionStatus.FAILURE) {
            status = TransactionStatus.SUCCESS;
        }
    }

    protected abstract void verifyAccount();
    protected abstract void process();
    protected abstract void updateBalance();
    protected abstract void generateReceipt();

    public int getTransactionId() { return transactionId; }
    public Date getCreationTime() { return creationTime; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ", status=" + status + "}";
    }
}
