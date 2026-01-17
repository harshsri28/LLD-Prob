package org.example.models;

public class Movie {
    String id;
    String title;
    int duration;
    public Movie(String id, String title, int duration) {
        this.id = id;
        this.title = title;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }
}
