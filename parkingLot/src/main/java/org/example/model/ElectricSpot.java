package org.example.model;

import org.example.enums.ParkingSpotType;

public class ElectricSpot extends ParkingSpot {
    public ElectricSpot(String spotNumber) {
        super(ParkingSpotType.ELECTRIC, spotNumber);
    }
}