package org.example.factory;

import org.example.model.Admin;
import org.example.model.Account;
import org.example.model.Movie;
import org.example.model.Show;
import org.example.model.CinemaHall;
import java.util.Date;

public class AdminFactory {
    public Admin createAdmin(String name, Account account) {
        return new Admin(name, account);
    }

    public Movie createMovie(String title, String description, Admin admin) {
        return new Movie(title, description, admin);
    }

    public Show createShow(Movie movie, Date startTime, CinemaHall hall) {
        return new Show(movie, startTime, hall);
    }
}
