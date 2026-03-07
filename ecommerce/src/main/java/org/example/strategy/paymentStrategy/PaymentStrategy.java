package org.example.strategy.paymentStrategy;

import org.example.models.Order;

public interface PaymentStrategy {
    boolean processPayment(Order order);
}
