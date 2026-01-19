package org.example.models;

import org.example.enums.VehicleStatus;
import org.example.enums.VehicleType;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Vehicle {
    String licensePlate;
    VehicleStatus status;
    VehicleType type;
    double pricePerHour;
    double pricePerKm;
    int bookingCount = 0;
    AtomicBoolean isBooked = new AtomicBoolean(false);

    public Vehicle(String licensePlate, double pricePerHour, double pricePerKm, VehicleType type) {
        this.licensePlate = licensePlate;
        this.pricePerHour = pricePerHour;
        this.pricePerKm = pricePerKm;
        this.type = type;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void incrementBookingCount() {
        this.bookingCount++;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    public AtomicBoolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(AtomicBoolean isBooked) {
        this.isBooked = isBooked;
    }
}
