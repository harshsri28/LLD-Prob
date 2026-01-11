package org.example.strategy.ticketState;

public class ActiveTicketState implements TicketState {
    public boolean canExit() {
        return false;
    }
}