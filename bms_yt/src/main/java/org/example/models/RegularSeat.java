package org.example.models;

import org.example.enums.SeatType;

public class RegularSeat extends Seat {
    public RegularSeat(String id, double price) {
        super(id, price);
    }

    public SeatType getType() {
        return SeatType.REGULAR;
    }
}
