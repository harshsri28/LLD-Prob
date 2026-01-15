package org.example.models;

import org.example.enums.Specialization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Doctor {
    UUID id;
    String name;
    Specialization specialization;
    Map<String,Boolean> availability = new ConcurrentHashMap<>();
    double rating;

    public Doctor(String name, Specialization specialization, double ratings) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.specialization = specialization;
        this.rating = ratings;
    }

    public UUID getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public Map<String, Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(Map<String, Boolean> availability) {
        this.availability = availability;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
