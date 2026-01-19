package org.example.models;

import org.example.enums.VehicleType;

public class Sedan extends Vehicle {
    public Sedan(String licensePlate, double pricePerHour, double pricePerKm) {
        super(licensePlate, pricePerHour, pricePerKm, VehicleType.SEDAN);
    }
}
