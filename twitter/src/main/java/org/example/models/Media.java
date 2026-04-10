package org.example.models;

import org.example.enums.MediaType;

import java.util.concurrent.atomic.AtomicLong;

public class Media {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private long id;
    private String url;
    private MediaType type;

    public Media(String url, MediaType type) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.url = url;
        this.type = type;
    }

    // Getters
    public long getId() { return id; }
    public String getUrl() { return url; }
    public MediaType getType() { return type; }

    @Override
    public String toString() {
        return "Media{" + type + ": " + url + "}";
    }
}
