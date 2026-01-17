package org.example.model;

public class FrontDeskOfficer extends Person {
    public FrontDeskOfficer(String name, Account account) {
        super(name, account);
    }

    public boolean createBooking(Booking booking) {
        System.out.println("Booking created for show: " + booking.getShow().getMovie().getTitle());
        return true;
    }
}
