package org.example.models;

import org.example.enums.ShipmentStatus;

import java.util.Date;
import java.util.UUID;

public class Shipment {
    private String shipmentNumber;
    private Date shipmentDate;
    private Date estimatedArrival;
    private String shipmentMethod;
    private ShipmentStatus status;
    private String trackingNumber;
    private Address shippingAddress;

    public Shipment(String shipmentMethod, Address shippingAddress) {
        this.shipmentNumber = "SHP-" + UUID.randomUUID().toString().substring(0, 8);
        this.trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8);
        this.shipmentDate = new Date();
        this.shipmentMethod = shipmentMethod;
        this.shippingAddress = shippingAddress;
        this.status = ShipmentStatus.PENDING;
    }

    public void updateStatus(ShipmentStatus newStatus) {
        System.out.println("Shipment " + shipmentNumber + " status: " + this.status + " -> " + newStatus);
        this.status = newStatus;
    }

    public String getShipmentNumber() { return shipmentNumber; }
    public Date getShipmentDate() { return shipmentDate; }
    public Date getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(Date estimatedArrival) { this.estimatedArrival = estimatedArrival; }
    public String getShipmentMethod() { return shipmentMethod; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public String getTrackingNumber() { return trackingNumber; }
    public Address getShippingAddress() { return shippingAddress; }

    @Override
    public String toString() {
        return "Shipment{number='" + shipmentNumber + "', tracking='" + trackingNumber +
                "', status=" + status + ", method='" + shipmentMethod + "'}";
    }
}
