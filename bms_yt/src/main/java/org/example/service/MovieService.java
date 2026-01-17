package org.example.service;

import org.example.models.Movie;
import org.example.repository.MovieRepository;

public class MovieService {
    MovieRepository movieRepo;

    public MovieService(MovieRepository movieRepo) {
        this.movieRepo = movieRepo;
    }

    public Movie createMovie(String id, String title, int duration) {
        Movie movie = new Movie(id, title, duration);
        movieRepo.save(movie);
        return movie;
    }

    public Movie getMovie(String id) {
        return movieRepo.get(id);
    }
}
