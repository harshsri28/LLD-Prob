package org.example.strategy.ticketState;

public class PaidTicketState implements TicketState {
    public boolean canExit() {
        return true;
    }
}
