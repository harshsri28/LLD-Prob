package org.example.model;

import org.example.enums.ParkingSpotType;

public class CompactSpot extends ParkingSpot {
    public CompactSpot(String spotNumber) {
        super(ParkingSpotType.COMPACT, spotNumber);
    }
}
