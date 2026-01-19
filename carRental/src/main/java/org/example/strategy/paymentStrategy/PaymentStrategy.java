package org.example.strategy.paymentStrategy;

import org.example.models.Booking;

public interface PaymentStrategy {
    boolean processPayment(Booking booking);
}
