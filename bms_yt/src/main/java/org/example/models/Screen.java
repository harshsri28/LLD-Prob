package org.example.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Screen {
    String id;
    Map<String, Seat> seats = new HashMap<>();

    public Screen(String id) {
        this.id = id;
    }

    public void addSeats(Seat seat) {
        seats.put(seat.getId(), seat);
    }

    public List<Seat> getSeats() {
        return new ArrayList<>(seats.values());
    }

    public String getId() {
        return id;
    }
}
