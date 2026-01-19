package org.example.strategy.PricingStrategy;

import org.example.models.Vehicle;

import java.time.Duration;
import java.time.LocalDateTime;

public class HourlyPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime, double distanceKm) {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        long hours = (long) Math.ceil((double) minutes / 60);
        return vehicle.getPricePerHour() * hours;
    }
}
