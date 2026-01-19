package org.example.strategy.PricingStrategy;

import org.example.models.Vehicle;

import java.time.LocalDateTime;

public interface PricingStrategy {
    double calculatePrice(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime, double distanceKm);
}
