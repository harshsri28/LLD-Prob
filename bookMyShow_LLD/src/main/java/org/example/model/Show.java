package org.example.model;

import java.util.Date;
import java.util.Map;
import org.example.enums.SeatType;

public class Show {
    private Movie movie;
    private Date startTime;
    private Date endTime;
    private CinemaHall hall;
    private Map<SeatType, Double> seatPricing;

    public Show(Movie movie, Date startTime, CinemaHall hall) {
        this.movie = movie;
        this.startTime = startTime;
        this.hall = hall;
    }

    public Movie getMovie() {
        return movie;
    }
    
    public void setSeatPricing(Map<SeatType, Double> seatPricing) {
        this.seatPricing = seatPricing;
    }
    
    public double getPrice(SeatType seatType) {
        if (seatPricing != null && seatPricing.containsKey(seatType)) {
            return seatPricing.get(seatType);
        }
        return 0.0; // Default or throw exception
    }
}
