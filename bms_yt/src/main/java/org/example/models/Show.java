package org.example.models;

import java.util.Date;
import java.util.List;

public class Show {
    String id;
    Theatre theatre;
    Screen screen;
    Movie movie;
    Date startTime;
    Date endTime;

    public Show(String id, Theatre theatre, Screen screen, Movie movie, Date startTime) {
        this.id = id;
        this.theatre = theatre;
        this.screen = screen;
        this.movie = movie;
        this.startTime = startTime;
        this.endTime = new Date(startTime.getTime() + ((long) movie.getDuration() * 60 * 1000));
    }

    public List<Seat> getSeats() {
        return screen.getSeats();
    }

    @Override
    public String toString() {
        return "Show{" +
                "id='" + id + '\'' +
                ", theatre=" + theatre +
                ", screen=" + screen +
                ", movie=" + movie +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public String getId() {
        return id;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public Screen getScreen() {
        return screen;
    }

    public Movie getMovie() {
        return movie;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
