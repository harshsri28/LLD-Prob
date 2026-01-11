package org.example.model;

import org.example.enums.ParkingSpotType;

public class MotorbikeSpot extends ParkingSpot {
    public MotorbikeSpot(String spotNumber) {
        super(ParkingSpotType.MOTORCYCLE, spotNumber);
    }
}
