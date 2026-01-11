package org.example.factory;

import org.example.enums.ParkingSpotType;
import org.example.model.*;

public class ParkingSpotFactory {
    public static ParkingSpot createParkingSpot(ParkingSpotType type, String spotNumber) {
        switch (type) {
            case COMPACT:
                return new CompactSpot(spotNumber);
            case LARGE:
                return new LargeSpot(spotNumber);
            case HANDICAPPED:
                return new HandicappedSpot(spotNumber);
            case ELECTRIC:
                return new ElectricSpot(spotNumber);
            case MOTORCYCLE:
                return new MotorbikeSpot(spotNumber);
            default:
                throw new IllegalArgumentException("Invalid Spot Type");
        }
    }
}
