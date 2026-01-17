package org.example.models;

import org.example.enums.SeatType;

public class ReclinerSeat extends Seat {
    public ReclinerSeat(String id, double price) {
        super(id, price);
    }

    public SeatType type(){
        return SeatType.RECLINER;
    }
}
