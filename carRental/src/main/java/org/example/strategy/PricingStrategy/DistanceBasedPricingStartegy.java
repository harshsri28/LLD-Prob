package org.example.strategy.PricingStrategy;

import org.example.models.Vehicle;

import java.time.LocalDateTime;

public class DistanceBasedPricingStartegy implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime, double distanceKm) {
        return vehicle.getPricePerKm() * distanceKm;
    }
}
