package org.example.strategy.parkingFee;

public class PerHourPricingStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(int hours) {
        if (hours <= 1) return 4.0;
        if (hours <= 3) return 4.0 + (hours - 1) * 3.5;
        return 4.0 + 2 * 3.5 + (hours - 3) * 2.5;
    }
}
