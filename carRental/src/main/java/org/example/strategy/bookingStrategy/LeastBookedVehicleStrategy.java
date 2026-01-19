package org.example.strategy.bookingStrategy;

import org.example.models.Vehicle;

import java.util.List;

import java.util.stream.Collectors;

public class LeastBookedVehicleStrategy implements BookingStrategy {

    @Override
    public Vehicle bookVehicle(List<Vehicle> vehicles) {
        List<Vehicle> sortedVehicles = vehicles.stream().sorted((v1,v2) -> v1.getBookingCount() - v2.getBookingCount()).collect(Collectors.toList()); // sort by booking count

        for(Vehicle vehicle : sortedVehicles){
            if(vehicle.getIsBooked().compareAndSet(false, true)){
                return vehicle;
            }
        }
        return null;

    }
}
