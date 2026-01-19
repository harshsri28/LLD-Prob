package org.example.services;

import org.example.enums.PaymentStatus;
import org.example.models.Booking;
import org.example.strategy.paymentStrategy.PaymentStrategy;

public class PaymentProcessor {
    PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public  boolean pay(Booking booking) {
        boolean paymentStatus = paymentStrategy.processPayment(booking);

        if(paymentStatus) {
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
            System.out.println("Payment successful for booking: " + booking.getBookingId());
        }else{
            booking.setPaymentStatus(PaymentStatus.FAILED);
            System.out.println("Payment failed for booking: " + booking.getBookingId());
        }

        return paymentStatus;
    }
}
