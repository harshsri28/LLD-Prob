package org.example.model;

import org.example.enums.ParkingSpotType;
import org.example.enums.VehicleType;
import org.example.strategy.ParkingSpotObserver.ParkingSpotObserver;

public abstract class ParkingSpot {
    private String spotNumber;
    private boolean available = true;
    private Vehicle vehicle;
    private final ParkingSpotType type;
    private ParkingSpotObserver displayBoard;

    public ParkingSpot(ParkingSpotType type, String spotNumber) {
        this.type = type;
        this.spotNumber = spotNumber;
    }

    public void setObserver(ParkingSpotObserver observer) {
        this.displayBoard = observer;
    }

    public boolean assignVehicle(Vehicle vehicle) {
        if (!this.isAvailable()) return false;
        this.vehicle = vehicle;
        available = false;
        if (displayBoard != null) {
            displayBoard.update("Spot " + spotNumber + " occupied");
        }
        return true;
    }

    public boolean removeVehicle() {
        this.vehicle = null;
        available = true;
        if (displayBoard != null) {
            displayBoard.update("Spot " + spotNumber + " available");
        }
        return true;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isFree() {
        return available;
    }

    public String getSpotNumber() {
        return spotNumber;
    }

    public String getNumber() {
        return spotNumber;
    }

    public ParkingSpotType getType() {
        return type;
    }

    public boolean isSuitableFor(VehicleType vehicleType) {
        return type.isSuitableFor(vehicleType);
    }
}
