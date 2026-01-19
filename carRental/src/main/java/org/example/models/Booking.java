package org.example.models;

import org.example.enums.BookingStatus;
import org.example.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Booking {
    String bookingId;
    User user;
    Vehicle vehicle;
    Branch pickUpBranch;
    Branch dropOffBranch;
    LocalDateTime startTime;
    LocalDateTime endTime;
    double distanceKm;

    BookingStatus status = BookingStatus.CREATED;
    PaymentStatus paymentStatus = PaymentStatus.PENDING;
    double amount;

    public Booking(String bookingId, User user, Vehicle vehicle, Branch pickUpBranch, Branch dropOffBranch, LocalDateTime startTime, LocalDateTime endTime, double amount) {
        this.bookingId = bookingId;
        this.user = user;
        this.vehicle = vehicle;
        this.pickUpBranch = pickUpBranch;
        this.dropOffBranch = dropOffBranch;
        this.startTime = startTime;
        this.amount = amount;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Branch getPickUpBranch() {
        return pickUpBranch;
    }

    public void setPickUpBranch(Branch pickUpBranch) {
        this.pickUpBranch = pickUpBranch;
    }

    public Branch getDropOffBranch() {
        return dropOffBranch;
    }

    public void setDropOffBranch(Branch dropOffBranch) {
        this.dropOffBranch = dropOffBranch;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM h:mm yyyy ");
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", user=" + user +
                ", vehicle=" + vehicle +
                ", pickUpBranch=" + pickUpBranch +
                ", dropOffBranch=" + dropOffBranch +
                ", startTime=" + startTime.format(formatter) +
                ", endTime=" + endTime.format(formatter) +
                ", distanceKm=" + distanceKm +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", amount=" + amount +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.user = builder.user;
        this.vehicle = builder.vehicle;
        this.pickUpBranch = builder.pickUpBranch;
        this.dropOffBranch = builder.dropOffBranch;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.distanceKm = builder.distanceKm;
        this.amount = builder.amount;
        // Generate bookingId if not provided
        if (this.bookingId == null) {
            this.bookingId = UUID.randomUUID().toString();
        }
    }

    public static class Builder {
        private String bookingId;
        private User user;
        private Vehicle vehicle;
        private Branch pickUpBranch;
        private Branch dropOffBranch;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private double distanceKm;
        private double amount;

        public Builder bookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public Builder pickUpBranch(Branch pickUpBranch) {
            this.pickUpBranch = pickUpBranch;
            return this;
        }

        public Builder dropOffBranch(Branch dropOffBranch) {
            this.dropOffBranch = dropOffBranch;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder distanceKm(double distanceKm) {
            this.distanceKm = distanceKm;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }

}
