package org.example.strategy.paymentStrategy;

import org.example.model.ParkingTicket;

public interface PaymentStrategy {
    void pay(ParkingTicket ticket);
}
