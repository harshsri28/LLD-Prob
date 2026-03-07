package org.example.models;

import org.example.enums.OrderStatus;

import java.util.Date;

public class OrderLog {
    private String orderNumber;
    private Date createdDate;
    private OrderStatus status;
    private String message;

    public OrderLog(String orderNumber, OrderStatus status, String message) {
        this.orderNumber = orderNumber;
        this.createdDate = new Date();
        this.status = status;
        this.message = message;
    }

    public String getOrderNumber() { return orderNumber; }
    public Date getCreatedDate() { return createdDate; }
    public OrderStatus getStatus() { return status; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "OrderLog{orderNumber='" + orderNumber + "', status=" + status +
                ", message='" + message + "', date=" + createdDate + "}";
    }
}
