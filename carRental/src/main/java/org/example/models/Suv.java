package org.example.models;

import org.example.enums.VehicleType;

public class Suv extends Vehicle {
    public Suv(String licensePlate, double pricePerHour, double pricePerKm) {
        super(licensePlate, pricePerHour, pricePerKm, VehicleType.SUV);
    }
}
