package org.example.models;

import org.example.enums.TransactionStatus;

public class TransactionResult {
    private TransactionStatus status;
    private String message;
    private double amount;

    private TransactionResult(TransactionStatus status, String message, double amount) {
        this.status = status;
        this.message = message;
        this.amount = amount;
    }

    public static TransactionResult success(String message) {
        return new TransactionResult(TransactionStatus.SUCCESS, message, 0);
    }

    public static TransactionResult success(String message, double amount) {
        return new TransactionResult(TransactionStatus.SUCCESS, message, amount);
    }

    public static TransactionResult failed(String message) {
        return new TransactionResult(TransactionStatus.FAILURE, message, 0);
    }

    public boolean isSuccess() {
        return status == TransactionStatus.SUCCESS;
    }

    public TransactionStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "TransactionResult{status=" + status + ", message='" + message + "'}";
    }
}
