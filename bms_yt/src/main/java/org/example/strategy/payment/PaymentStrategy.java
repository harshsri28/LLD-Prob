package org.example.strategy.payment;

import org.example.models.Booking;

public interface PaymentStrategy {
    boolean pay(Booking booking);
}
