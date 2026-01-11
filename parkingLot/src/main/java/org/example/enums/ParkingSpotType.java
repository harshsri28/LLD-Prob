package org.example.enums;

public enum ParkingSpotType {
    HANDICAPPED("Handicapped"),
    COMPACT("Compact"),
    LARGE("Large"),
    MOTORCYCLE("Motorcycle"),
    ELECTRIC("Electric Charging");

    String displayName;

    ParkingSpotType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSuitableFor(VehicleType vehicleType) {
        return vehicleType.canFitInSpot(this);
    }
}
