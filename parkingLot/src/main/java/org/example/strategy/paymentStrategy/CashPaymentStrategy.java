package org.example.strategy.paymentStrategy;

import org.example.enums.ParkingTicketStatus;
import org.example.model.ParkingTicket;

public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(ParkingTicket ticket) {
        System.out.println("Processing cash payment for ticket: " + ticket.getTicketNumber());
        ticket.updateStatus(ParkingTicketStatus.PAID);
    }
}
