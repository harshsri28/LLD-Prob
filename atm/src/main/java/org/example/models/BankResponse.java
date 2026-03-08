package org.example.models;

import org.example.enums.TransactionStatus;

public class BankResponse {
    private TransactionStatus status;
    private String message;

    public BankResponse(TransactionStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static BankResponse success(String message) {
        return new BankResponse(TransactionStatus.SUCCESS, message);
    }

    public static BankResponse error(String message) {
        return new BankResponse(TransactionStatus.FAILURE, message);
    }

    public boolean isSuccess() {
        return status == TransactionStatus.SUCCESS;
    }

    public TransactionStatus getStatus() { return status; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "BankResponse{status=" + status + ", message='" + message + "'}";
    }
}
