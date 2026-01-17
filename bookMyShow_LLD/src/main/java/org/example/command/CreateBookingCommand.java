package org.example.command;

import org.example.model.Booking;
import org.example.model.FrontDeskOfficer;

public class CreateBookingCommand implements Command {
    private FrontDeskOfficer officer;
    private Booking booking;

    public CreateBookingCommand(FrontDeskOfficer officer, Booking booking) {
        this.officer = officer;
        this.booking = booking;
    }

    @Override
    public void execute() {
        officer.createBooking(booking);
    }
}
