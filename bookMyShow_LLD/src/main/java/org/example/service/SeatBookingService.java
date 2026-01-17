package org.example.service;

import org.example.model.ShowSeat;
import org.example.model.Booking;
import org.example.model.Show;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeatBookingService {
    private static final long LOCK_TIMEOUT_MS = 10000; // 10 seconds for demo (usually 10 mins)

    public boolean reserveSeats(Show show, List<ShowSeat> seats) {
        // Try to acquire lock on all seats
        for (ShowSeat seat : seats) {
             if (seat.isBooked() || seat.isReserved()) {
                 return false; // Already booked/reserved
             }
        }
        
        // Optimistic locking simulation or explicit locking
        boolean allLocked = true;
        for (ShowSeat seat : seats) {
            try {
                if (!seat.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                    allLocked = false;
                    break;
                }
            } catch (InterruptedException e) {
                allLocked = false;
                break;
            }
        }
        
        if (allLocked) {
            try {
                 // Double check inside lock
                 for (ShowSeat seat : seats) {
                     if (seat.isBooked() || seat.isReserved()) {
                         // Rollback
                         return false; 
                     }
                 }
                 
                 // Reserve
                 for (ShowSeat seat : seats) {
                     seat.setReserved(true);
                 }
                 return true;
            } finally {
                for (ShowSeat seat : seats) {
                    if (seat.getLock().isHeldByCurrentThread()) {
                        seat.getLock().unlock();
                    }
                }
            }
        } else {
             // Release any locks acquired
             for (ShowSeat seat : seats) {
                 if (seat.getLock().isHeldByCurrentThread()) {
                     seat.getLock().unlock();
                 }
             }
             return false;
        }
    }
}
