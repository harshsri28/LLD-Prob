package org.example.model;

import java.util.ArrayList;
import java.util.List;
import org.example.service.Observer;

public class Customer extends Person implements Observer {
    private List<Booking> bookings;

    public Customer(String name, Account account) {
        super(name, account);
        bookings = new ArrayList<>();
    }

    @Override
    public void update(String message) {
        System.out.println("Notification for " + getName() + ": " + message);
    }

    public boolean makeBooking(Booking booking) {
        bookings.add(booking);
        System.out.println("Booking made successfully.");
        return true;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}
