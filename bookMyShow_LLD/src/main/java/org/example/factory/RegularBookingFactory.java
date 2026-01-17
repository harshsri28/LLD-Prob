package org.example.factory;

import org.example.model.Booking;
import org.example.model.Show;
import org.example.model.ShowSeat;
import java.util.List;

public class RegularBookingFactory extends BookingFactory {
    @Override
    public Booking createBooking(Show show, List<ShowSeat> seats) {
        return new Booking(show, seats);
    }
}
