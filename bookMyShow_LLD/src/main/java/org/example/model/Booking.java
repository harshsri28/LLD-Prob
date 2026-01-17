package org.example.model;

import java.util.Date;
import java.util.List;
import org.example.enums.BookingStatus;
import org.example.strategy.PaymentStrategy;

public class Booking {
    private String bookingNumber;
    private int numberOfSeats;
    private Date createdOn;
    private BookingStatus status;
    private Show show;
    private List<ShowSeat> seats;
    private PaymentStrategy payment;
    private double totalAmount;

    public Booking(Show show, List<ShowSeat> seats) {
        this.show = show;
        this.seats = seats;
        this.status = BookingStatus.REQUESTED;
        this.createdOn = new Date();
        this.numberOfSeats = seats.size();
        calculateTotalAmount();
    }
    
    private void calculateTotalAmount() {
        this.totalAmount = 0;
        for (ShowSeat seat : seats) {
            this.totalAmount += show.getPrice(seat.getSeatType());
        }
    }

    public Show getShow() {
        return show;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean makePayment(PaymentStrategy payment) {
        this.payment = payment;
        boolean success = payment.pay(totalAmount);
        if (success) {
            this.status = BookingStatus.CONFIRMED;
            // Mark seats as permanently booked
            for(ShowSeat seat : seats) {
                seat.setBooked(true);
            }
        } else {
            this.status = BookingStatus.PENDING;
        }
        return success;
    }

    public boolean cancel() {
        this.status = BookingStatus.CANCELED;
        // Release seats
        for(ShowSeat seat : seats) {
            seat.setBooked(false);
            seat.setReserved(false);
        }
        System.out.println("Booking has been canceled.");
        return true;
    }
    
    public void expire() {
         if (this.status == BookingStatus.REQUESTED || this.status == BookingStatus.PENDING) {
             this.status = BookingStatus.EXPIRED;
             for(ShowSeat seat : seats) {
                seat.setReserved(false);
             }
             System.out.println("Booking expired due to timeout.");
         }
    }

    public boolean assignSeats(List<ShowSeat> seats) {
        this.seats = seats;
        System.out.println("Seats assigned.");
        return true;
    }
}
