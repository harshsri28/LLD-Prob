package org.example.services;

import org.example.enums.PaymentStatus;
import org.example.models.Order;
import org.example.strategy.paymentStrategy.PaymentStrategy;

public class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public boolean pay(Order order) {
        boolean success = paymentStrategy.processPayment(order);

        if (success) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            System.out.println("Payment successful for order: " + order.getOrderNumber());
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            System.out.println("Payment failed for order: " + order.getOrderNumber());
        }

        return success;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}
