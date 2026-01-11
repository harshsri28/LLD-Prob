package org.example.model;

import org.example.enums.ParkingSpotType;

public class HandicappedSpot extends ParkingSpot {
    public HandicappedSpot(String spotNumber) {
        super(ParkingSpotType.HANDICAPPED, spotNumber);
    }
}
