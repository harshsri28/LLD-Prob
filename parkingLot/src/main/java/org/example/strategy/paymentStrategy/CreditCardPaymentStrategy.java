package org.example.strategy.paymentStrategy;

import org.example.enums.ParkingTicketStatus;
import org.example.model.ParkingTicket;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(ParkingTicket ticket) {
        System.out.println("Processing credit card payment for ticket: " + ticket.getTicketNumber());
        ticket.updateStatus(ParkingTicketStatus.PAID);
    }
}
