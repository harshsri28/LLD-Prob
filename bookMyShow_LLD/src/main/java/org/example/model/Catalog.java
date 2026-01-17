package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private List<Movie> movies;

    public Catalog() {
        movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }
    
    public List<Movie> getMovies() {
        return movies;
    }

    public List<Movie> searchByTitle(String title) {
        List<Movie> results = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(movie);
            }
        }
        return results;
    }

    public List<Movie> searchByGenre(String genre) {
        List<Movie> results = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getGenre() != null && movie.getGenre().equalsIgnoreCase(genre)) {
                results.add(movie);
            }
        }
        return results;
    }

    public List<Movie> searchByLanguage(String language) {
        List<Movie> results = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getLanguage() != null && movie.getLanguage().equalsIgnoreCase(language)) {
                results.add(movie);
            }
        }
        return results;
    }
}
