package org.example.strategy.payment;

import org.example.models.Booking;

public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean pay(Booking booking) {
        System.out.println("booking " + booking.getBookingId() + " Paid via Card");
        return true;
    }
}
