package org.example.models;

import org.example.enums.ReturnStatus;

import java.util.UUID;

public class ReturnRequest {
    private String returnId;
    private String orderId;
    private String reason;
    private ReturnStatus status;
    private double refundAmount;

    public ReturnRequest(String orderId, String reason, double refundAmount) {
        this.returnId = "RET-" + UUID.randomUUID().toString().substring(0, 8);
        this.orderId = orderId;
        this.reason = reason;
        this.status = ReturnStatus.REQUESTED;
        this.refundAmount = refundAmount;
    }

    public void approve() {
        this.status = ReturnStatus.APPROVED;
        System.out.println("Return request " + returnId + " approved for order " + orderId);
    }

    public void reject() {
        this.status = ReturnStatus.REJECTED;
        System.out.println("Return request " + returnId + " rejected for order " + orderId);
    }

    public String getReturnId() { return returnId; }
    public String getOrderId() { return orderId; }
    public String getReason() { return reason; }
    public ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnStatus status) { this.status = status; }
    public double getRefundAmount() { return refundAmount; }

    @Override
    public String toString() {
        return "ReturnRequest{id='" + returnId + "', orderId='" + orderId +
                "', status=" + status + ", refund=$" + refundAmount + "}";
    }
}
