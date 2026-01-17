package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Cinema {
    private String name;
    private Address address;
    private List<CinemaHall> halls;

    public Cinema(String name, Address address) {
        this.name = name;
        this.address = address;
        this.halls = new ArrayList<>();
    }

    public void addHall(CinemaHall hall) {
        halls.add(hall);
    }
    
    public List<CinemaHall> getHalls() {
        return halls;
    }

    public String getName() {
        return name;
    }
}
