package org.example.models;

import org.example.enums.BookingStatus;
import org.example.enums.PaymentType;

import java.util.List;

public class Booking {
    String bookingId;
    String showId;
    String userId;
    List<String> seatIds;
    BookingStatus status;
    PaymentType paymentType;
    double amount;

    public Booking(String bookingId, String showId, String userId, List<String> seatIds, BookingStatus status, PaymentType paymentType, double amount) {
        this.bookingId = bookingId;
        this.showId = showId;
        this.userId = userId;
        this.seatIds = seatIds;
        this.status = status;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", showId='" + showId + '\'' +
                ", userId='" + userId + '\'' +
                ", seatIds=" + seatIds +
                ", status=" + status +
                ", paymentType=" + paymentType +
                ", amount=" + amount +
                '}';
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getShowId() {
        return showId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
