package org.example.model;

import org.example.enums.ParkingSpotType;

public class LargeSpot extends ParkingSpot {
    public LargeSpot(String spotNumber) {
        super(ParkingSpotType.LARGE, spotNumber);
    }
}
