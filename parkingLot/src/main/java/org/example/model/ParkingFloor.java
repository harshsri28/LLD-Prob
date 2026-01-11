package org.example.model;

import org.example.enums.VehicleType;

import java.util.concurrent.ConcurrentHashMap;

public class ParkingFloor {
    private String name;
    private ConcurrentHashMap<String, ParkingSpot> availableSpots;

    public ParkingFloor(String name) {
        this.name = name;
        this.availableSpots = new ConcurrentHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addSpot(ParkingSpot spot) {
        availableSpots.put(spot.getSpotNumber(), spot);
    }

    public ParkingSpot assignSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : availableSpots.values()) {
            if (spot.isAvailable() && spot.isSuitableFor(vehicleType)) {
                return spot;
            }
        }
        return null;
    }

    public int getAvailableSpotCount(VehicleType vehicleType) {
        int count = 0;
        for (ParkingSpot spot : availableSpots.values()) {
            if (spot.isAvailable() && spot.isSuitableFor(vehicleType)) {
                count++;
            }
        }
        return count;
    }
}
