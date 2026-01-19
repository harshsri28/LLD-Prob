package org.example.strategy.paymentStrategy;

import org.example.models.Booking;

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public boolean processPayment(Booking booking) {
        System.out.println("Processing credit card payment for booking: " + booking.getBookingId());
        return true;
    }
}
