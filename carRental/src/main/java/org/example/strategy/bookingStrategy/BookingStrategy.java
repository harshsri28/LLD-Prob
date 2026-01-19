package org.example.strategy.bookingStrategy;

import org.example.models.Vehicle;

import java.util.List;

public interface BookingStrategy {
    Vehicle bookVehicle(List<Vehicle> vehicles);
}
