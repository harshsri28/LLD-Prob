package org.example.repository;

import org.example.models.Booking;

import java.util.*;

public class BookingRepository {
    Map<String, Booking> bookings = new HashMap<>();

    public void addBooking(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public void removeBooking(String bookingId) {
        bookings.remove(bookingId);
    }

    public List<Booking> getAllBookings(){
        return new ArrayList<>(bookings.values());
    }
}
