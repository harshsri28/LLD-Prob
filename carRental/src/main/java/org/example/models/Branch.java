package org.example.models;

import org.example.enums.VehicleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Branch {
    String id;
    String city;

    Map<VehicleType, List<Vehicle>> vehicles = new HashMap<>();

    public Branch(String id, String city) {
        this.id = id;
        this.city = city;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.computeIfAbsent(vehicle.getType(), k -> new ArrayList<>()).add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.get(vehicle.getType()).remove(vehicle);
    }

    public List<Vehicle> getVehiclesByType(VehicleType type) { return vehicles.get(type); }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<VehicleType, List<Vehicle>> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Map<VehicleType, List<Vehicle>> vehicles) {
        this.vehicles = vehicles;
    }
}
