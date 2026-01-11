package org.example.model;

public class ElectricCarFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new ElectricCar();
    }
}
