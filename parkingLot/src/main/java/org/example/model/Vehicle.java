package org.example.model;

import org.example.enums.VehicleType;

public abstract class Vehicle {
    private VehicleType type;
    private ParkingTicket ticket;
    private String licensePlate;

    public Vehicle(VehicleType type) {
        this.type = type;
    }

    public VehicleType getType() {
        return type;
    }

    public void assignTicket(ParkingTicket ticket) {
        this.ticket = ticket;
    }

    public ParkingTicket getTicket() {
        return ticket;
    }
}
