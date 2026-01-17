package org.example.model;

import org.example.enums.SeatType;
import java.util.concurrent.locks.ReentrantLock;

public class ShowSeat {
    private String seatNumber;
    private SeatType seatType;
    private boolean isBooked;
    private boolean isReserved; // For temporary locking
    private long reservationTimestamp; // When the seat was reserved
    private ReentrantLock lock;

    public ShowSeat(String seatNumber, SeatType seatType) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = false;
        this.isReserved = false;
        this.lock = new ReentrantLock();
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public SeatType getSeatType() {
        return seatType;
    }

    public boolean isBooked() {
        return isBooked;
    }
    
    public boolean isReserved() {
        return isReserved;
    }
    
    public void setBooked(boolean booked) {
        isBooked = booked;
        isReserved = false; // Clear reservation once booked
    }
    
    public void setReserved(boolean reserved) {
        isReserved = reserved;
        if (reserved) {
            reservationTimestamp = System.currentTimeMillis();
        }
    }
    
    public long getReservationTimestamp() {
        return reservationTimestamp;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
