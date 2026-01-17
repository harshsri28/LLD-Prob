package org.example.factory;

import org.example.model.*;
import java.util.Date;
import java.util.List;

public abstract class BookingFactory {
    public abstract Booking createBooking(Show show, List<ShowSeat> seats);
}
