package org.example.strategy.paymentStrategy;

import org.example.models.Order;

public class BankTransferPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Order order) {
        System.out.println("Processing bank transfer payment of $" + order.getTotalAmount() +
                " for order: " + order.getOrderNumber());
        // Simulate payment processing
        return true;
    }
}
