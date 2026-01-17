package org.example.model;

public class Admin extends Person {
    public Admin(String name, Account account) {
        super(name, account);
    }

    public boolean addMovie(Movie movie) {
        System.out.println("Movie " + movie.getTitle() + " added.");
        return true;
    }

    public boolean addShow(Show show) {
        System.out.println("Show added for movie: " + show.getMovie().getTitle());
        return true;
    }

    public boolean blockUser(Customer customer) {
        System.out.println("Customer " + customer.getName() + " blocked.");
        return true;
    }
}
