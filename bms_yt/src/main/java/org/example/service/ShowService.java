package org.example.service;

import org.example.models.Movie;
import org.example.models.Screen;
import org.example.models.Show;
import org.example.models.Theatre;
import org.example.repository.MovieRepository;
import org.example.repository.ShowRepository;
import org.example.repository.TheatreRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ShowService {
    ShowRepository showRepo;
    TheatreRepository theatreRepo;
    MovieRepository movieRepo;

    public ShowService(ShowRepository showRepo, TheatreRepository theatreRepo, MovieRepository movieRepo) {
        this.showRepo = showRepo;
        this.theatreRepo = theatreRepo;
        this.movieRepo = movieRepo;
    }

    public Show createShow(String id, String theatreId, String screenId, String movieId, Date startTime){
        Theatre theatre = theatreRepo.get(theatreId);
        Screen screen = theatre.getScreen(screenId);
        Movie movie = movieRepo.get(movieId);
        Show show = new Show(id, theatre, screen, movie, startTime);
        showRepo.save(show);
        return show;
    }

    public  Show getShow(String id) {
        return showRepo.get(id);
    }

    public List<Show> getShowsByMovieTitle(String title){
        return showRepo.getAll().stream().
                filter(show -> show.getMovie().getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }
}
