package org.example.strategy.parkingFee;

public class ElectricCarChargingFeeStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(int hours) {
        double parkingFee = new PerHourPricingStrategy().calculateFee(hours);
        double chargingFee = hours * 2.0;
        return parkingFee + chargingFee;
    }
}
