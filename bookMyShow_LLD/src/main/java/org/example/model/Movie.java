package org.example.model;

public class Movie {
    private String title;
    private String description;
    private Admin movieAddedBy;
    private String genre;
    private String language;

    public Movie(String title, String description, Admin movieAddedBy) {
        this.title = title;
        this.description = description;
        this.movieAddedBy = movieAddedBy;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
}
