package org.example.model;

import java.util.ArrayList;
import java.util.List;
import org.example.enums.SeatType;

public class CinemaHall {
    private String hallName;
    private String hallNumber;
    private int totalSeats;
    private List<ShowSeat> seats;

    public CinemaHall() {
        seats = new ArrayList<>();
    }

    public CinemaHall(String hallName, String hallNumber, int totalSeats) {
        this.hallName = hallName;
        this.hallNumber = hallNumber;
        this.totalSeats = totalSeats;
        seats = new ArrayList<>();
        initializeSeats();
    }

    private void initializeSeats() {
        // Initialize seats with default values
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new ShowSeat("A" + i, SeatType.REGULAR));
        }
    }

    public String getHallName() {
        return hallName;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public List<ShowSeat> getSeats() {
        return seats;
    }
}
