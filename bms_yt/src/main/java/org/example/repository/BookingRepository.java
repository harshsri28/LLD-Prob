package org.example.repository;

import org.example.models.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRepository {
    Map<String, Booking> map = new HashMap<>();

    public void save(Booking booking) {
        map.put(booking.getBookingId(), booking);
    }

    public Booking get(String id) {
        return map.get(id);
    }

    public List<Booking> getBookingsByShow(String showId) {
        List<Booking> showBookings = new ArrayList<>();
        for (Booking booking : map.values()) {
            if (booking.getShowId().equals(showId)) {
                showBookings.add(booking);
            }
        }
        return showBookings;
    }
}
