package org.example.models;

import org.example.enums.PaymentStatus;

import java.util.UUID;

public class Refund {
    private String refundId;
    private String returnRequestId;
    private double amount;
    private PaymentStatus status;

    public Refund(String returnRequestId, double amount) {
        this.refundId = "REF-" + UUID.randomUUID().toString().substring(0, 8);
        this.returnRequestId = returnRequestId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void processRefund() {
        this.status = PaymentStatus.REFUNDED;
        System.out.println("Refund " + refundId + " of $" + amount + " processed successfully.");
    }

    public String getRefundId() { return refundId; }
    public String getReturnRequestId() { return returnRequestId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Refund{id='" + refundId + "', amount=$" + amount + ", status=" + status + "}";
    }
}
