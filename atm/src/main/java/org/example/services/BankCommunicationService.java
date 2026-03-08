package org.example.services;

import org.example.models.BankResponse;
import org.example.transaction.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BankCommunicationService {
    private final ExecutorService connectionPool;
    private final long timeoutMs;

    public BankCommunicationService(int poolSize, long timeoutMs) {
        this.connectionPool = Executors.newFixedThreadPool(poolSize);
        this.timeoutMs = timeoutMs;
    }

    public BankResponse sendTransaction(Transaction transaction) {
        Future<BankResponse> future = connectionPool.submit(() -> {
            // Simulate network communication
            transaction.executeTransaction();
            return BankResponse.success("Transaction " + transaction.getTransactionId() + " processed.");
        });

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Transaction " + transaction.getTransactionId() + " timed out.");
            return BankResponse.error("Transaction timeout");
        } catch (Exception e) {
            future.cancel(true);
            System.out.println("Transaction " + transaction.getTransactionId() + " failed: " + e.getMessage());
            return BankResponse.error("Transaction failed: " + e.getMessage());
        }
    }

    public void shutdown() {
        connectionPool.shutdown();
        try {
            if (!connectionPool.awaitTermination(5, TimeUnit.SECONDS)) {
                connectionPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            connectionPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
