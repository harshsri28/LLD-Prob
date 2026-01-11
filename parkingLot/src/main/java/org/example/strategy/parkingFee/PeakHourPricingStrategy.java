package org.example.strategy.parkingFee;

public class PeakHourPricingStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(int hours) {
        return hours * 5.0;
    }
}
