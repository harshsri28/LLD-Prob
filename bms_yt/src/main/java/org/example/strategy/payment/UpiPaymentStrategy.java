package org.example.strategy.payment;

import org.example.models.Booking;
import org.example.strategy.payment.PaymentStrategy;

public class UpiPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean pay(Booking booking) {
        System.out.println("booking " + booking.getBookingId() + " Paid via UPI");
        return true;
    }
}
