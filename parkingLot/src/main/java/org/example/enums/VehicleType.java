package org.example.enums;

public enum VehicleType {
    CAR("Car"),
    TRUCK("Truck"),
    MOTORCYCLE("Motorcycle"),
    ELECTRIC("Electric Car"),
    VAN("Van");

    String description;

    VehicleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canFitInSpot(ParkingSpotType spotType) {
        switch (this){
            case TRUCK:
            case VAN:
                return spotType == ParkingSpotType.LARGE;
            case MOTORCYCLE:
                return spotType == ParkingSpotType.MOTORCYCLE;
            case ELECTRIC:
                return spotType == ParkingSpotType.ELECTRIC || spotType == ParkingSpotType.COMPACT;
            case CAR:
                return spotType == ParkingSpotType.COMPACT || spotType == ParkingSpotType.LARGE;
            default:
                return true;
        }
    }
}
