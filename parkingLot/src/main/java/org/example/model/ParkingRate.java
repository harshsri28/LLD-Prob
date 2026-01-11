package org.example.model;

public class ParkingRate {
    private double hourlyRate;
    private double dailyRate;

    public ParkingRate(double hourlyRate, double dailyRate) {
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
    }

    public double calculateCharges(long hours) {
        return hourlyRate * hours;
    }
}
