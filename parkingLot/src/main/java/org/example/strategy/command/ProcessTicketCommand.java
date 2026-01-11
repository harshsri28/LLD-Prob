package org.example.strategy.command;

import org.example.model.ParkingAttendant;

public class ProcessTicketCommand implements Command {
    private ParkingAttendant attendant;
    private String ticketNumber;

    public ProcessTicketCommand(ParkingAttendant attendant, String ticketNumber) {
        this.attendant = attendant;
        this.ticketNumber = ticketNumber;
    }

    @Override
    public void execute() {
        attendant.processTicket(ticketNumber);
    }
}

