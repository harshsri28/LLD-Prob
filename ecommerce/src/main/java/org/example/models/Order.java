package org.example.models;

import org.example.enums.OrderStatus;
import org.example.enums.PaymentStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderNumber;
    private OrderStatus status;
    private Date orderDate;
    private List<Item> items;
    private double totalAmount;
    private Address shippingAddress;
    private Shipment shipment;
    private PaymentStatus paymentStatus;
    private List<OrderLog> orderLogs;

    // Public constructor for simple creation
    public Order(String orderNumber) {
        this.orderNumber = orderNumber;
        this.status = OrderStatus.UNSHIPPED;
        this.orderDate = new Date();
        this.items = new ArrayList<>();
        this.orderLogs = new ArrayList<>();
        this.paymentStatus = PaymentStatus.UNPAID;
    }

    // Private constructor for Builder
    private Order(Builder builder) {
        this.orderNumber = builder.orderNumber != null ? builder.orderNumber : "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        this.status = OrderStatus.UNSHIPPED;
        this.orderDate = new Date();
        this.items = builder.items != null ? builder.items : new ArrayList<>();
        this.totalAmount = builder.totalAmount;
        this.shippingAddress = builder.shippingAddress;
        this.paymentStatus = PaymentStatus.UNPAID;
        this.orderLogs = new ArrayList<>();
    }

    public boolean sendForShipment() {
        if (status == OrderStatus.CANCELED) {
            System.out.println("Cannot ship a canceled order.");
            return false;
        }
        this.shipment = new Shipment("Standard", shippingAddress);
        this.status = OrderStatus.SHIPPED;
        addOrderLog(new OrderLog(orderNumber, OrderStatus.SHIPPED, "Order sent for shipment."));
        System.out.println("Order " + orderNumber + " sent for shipment.");
        return true;
    }

    public boolean cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.COMPLETED) {
            System.out.println("Cannot cancel order " + orderNumber + " - already " + status);
            return false;
        }
        this.status = OrderStatus.CANCELED;
        addOrderLog(new OrderLog(orderNumber, OrderStatus.CANCELED, "Order canceled."));
        System.out.println("Order " + orderNumber + " canceled.");
        return true;
    }

    public void addOrderLog(OrderLog log) {
        this.orderLogs.add(log);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getOrderNumber() { return orderNumber; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Date getOrderDate() { return orderDate; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public List<OrderLog> getOrderLogs() { return orderLogs; }

    @Override
    public String toString() {
        return "Order{number='" + orderNumber + "', status=" + status +
                ", items=" + items.size() + ", total=$" + totalAmount +
                ", payment=" + paymentStatus + "}";
    }

    public static class Builder {
        private String orderNumber;
        private List<Item> items;
        private double totalAmount;
        private Address shippingAddress;

        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder items(List<Item> items) { this.items = items; return this; }
        public Builder totalAmount(double totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder shippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; return this; }

        public Order build() {
            return new Order(this);
        }
    }
}
