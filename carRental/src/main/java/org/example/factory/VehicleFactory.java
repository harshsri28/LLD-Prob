package org.example.factory;

import org.example.enums.VehicleType;
import org.example.models.Sedan;
import org.example.models.Suv;
import org.example.models.Vehicle;

public class VehicleFactory {
    public static Vehicle getVehicle(VehicleType vehicleType, String licensePlate, double pricePerHour, double pricePerKm) {
        switch (vehicleType) {
            case SEDAN:
                return new Sedan(licensePlate, pricePerHour, pricePerKm);
            case SUV:
                return new Suv(licensePlate, pricePerHour, pricePerKm);
            default:
                return null;
        }
    }
}
