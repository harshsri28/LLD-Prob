package org.example.strategy.paymentStrategy;

import org.example.models.Booking;

public class WalletPayment implements PaymentStrategy {
    @Override
    public boolean processPayment(Booking booking) {
        System.out.println("Processing wallet payment for booking: " + booking.getBookingId());
        return true;
    }
}
