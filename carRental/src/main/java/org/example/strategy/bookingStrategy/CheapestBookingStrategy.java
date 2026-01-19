package org.example.strategy.bookingStrategy;

import org.example.enums.PricingStrategyType;
import org.example.models.Vehicle;

import java.util.List;

import java.util.stream.Collectors;

public class CheapestBookingStrategy implements BookingStrategy {
    PricingStrategyType pricingType;
    @Override
    public Vehicle bookVehicle(List<Vehicle> vehicles){
        List<Vehicle> sortedVehicles = vehicles.stream()
                .sorted((v1,v2) -> {
                    double val1 = pricingType == PricingStrategyType.TIME_BASED ? v1.getPricePerHour() : v1.getPricePerKm();
                    double val2 = pricingType == PricingStrategyType.TIME_BASED ? v2.getPricePerHour() : v2.getPricePerKm();
                    return Double.compare(val1, val2);
                }).collect(Collectors.toList());

        for(Vehicle vehicle : sortedVehicles){
            if(vehicle.getIsBooked().compareAndSet(false, true)){
                return vehicle;
            }
        }
        return null;
    }
}
