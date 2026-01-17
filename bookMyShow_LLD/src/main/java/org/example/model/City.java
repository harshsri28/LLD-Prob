package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class City {
    private String name;
    private String state;
    private String zipCode;
    private List<Cinema> cinemas;

    public City(String name, String state, String zipCode) {
        this.name = name;
        this.state = state;
        this.zipCode = zipCode;
        this.cinemas = new ArrayList<>();
    }

    public void addCinema(Cinema cinema) {
        cinemas.add(cinema);
    }

    public List<Cinema> getCinemas() {
        return cinemas;
    }
    
    public String getName() {
        return name;
    }
}
