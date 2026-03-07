package org.example.strategy.paymentStrategy;

import org.example.models.Order;

public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Order order) {
        System.out.println("Processing credit card payment of $" + order.getTotalAmount() +
                " for order: " + order.getOrderNumber());
        // Simulate payment processing
        return true;
    }
}
