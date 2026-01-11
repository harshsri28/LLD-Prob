package org.example.services;

import org.example.enums.VehicleType;
import org.example.exceptions.ParkingFullException;
import org.example.model.*;
import org.example.strategy.parkingFee.ParkingFeeStrategy;
import org.example.strategy.parkingFee.PerHourPricingStrategy;

import java.util.HashMap;
import java.util.Map;

public class ParkingLot {
    private static ParkingLot instance;
    private String name;
    private Address address;
    private ParkingRate parkingRate;
    private Map<String, ParkingFloor> parkingFloors;
    private Map<String, EntrancePanel> entrancePanels;
    private Map<String, ExitPanel> exitPanels;
    private Map<String, ParkingTicket> activeTickets;

    private ParkingLot() {
        parkingFloors = new HashMap<>();
        entrancePanels = new HashMap<>();
        exitPanels = new HashMap<>();
        activeTickets = new HashMap<>();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public synchronized ParkingTicket getNewParkingTicket(Vehicle vehicle) throws ParkingFullException {
        if (this.isFull(vehicle.getType())) {
            throw new ParkingFullException("Parking full for vehicle type: " + vehicle.getType());
        }

        ParkingFeeStrategy feeStrategy = new PerHourPricingStrategy();
        ParkingTicket ticket = new ParkingTicket(feeStrategy);
        vehicle.assignTicket(ticket);
        ticket.saveInDB();

        // Find and assign a spot
        ParkingSpot assignedSpot = findAvailableSpot(vehicle.getType());
        if (assignedSpot != null) {
            assignedSpot.assignVehicle(vehicle);
        }

        activeTickets.put(ticket.getTicketNumber(), ticket);
        return ticket;
    }

    public boolean isFull(VehicleType type) {
        for (ParkingFloor floor : parkingFloors.values()) {
            if (floor.getAvailableSpotCount(type) > 0) {
                return false;
            }
        }
        return true;
    }

    private ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        for (ParkingFloor floor : parkingFloors.values()) {
            ParkingSpot spot = floor.assignSpot(vehicleType);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public void addParkingFloor(ParkingFloor floor) {
        parkingFloors.put(floor.getName(), floor);
    }

    public void addEntrancePanel(EntrancePanel entrancePanel) {
        entrancePanels.put(entrancePanel.getId(), entrancePanel);
    }

    public void addExitPanel(ExitPanel exitPanel) {
        exitPanels.put(exitPanel.getId(), exitPanel);
    }
}
