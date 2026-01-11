package org.example.strategy.ParkingSpotObserver;

public class ParkingDisplayBoard implements ParkingSpotObserver {
    private String id;

    public ParkingDisplayBoard(String id) {
        this.id = id;
    }

    @Override
    public void update(String message) {
        System.out.println("Display Board " + id + " updated: " + message);
    }
}
