package org.example;

import org.example.model.*;
import org.example.factory.*;
import org.example.service.*;
import org.example.strategy.*;
import org.example.enums.*;
import org.example.command.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 1. Setup Data: City -> Cinema -> Hall
        City bangalore = new City("Bangalore", "Karnataka", "560001");
        Address cinemaAddress = new Address("Koramangala", "Bangalore", "Karnataka", "560034", "India");
        Cinema pvrCinema = new Cinema("PVR Koramangala", cinemaAddress);
        CinemaHall screen1 = new CinemaHall("Screen 1", "S1", 100);
        
        pvrCinema.addHall(screen1);
        bangalore.addCinema(pvrCinema);
        
        System.out.println("Setup complete for " + pvrCinema.getName() + " in " + bangalore.getName());

        // 2. Admin creates Movie and Show
        AdminFactory adminFactory = new AdminFactory();
        Account adminAccount = new Account();
        Admin admin = adminFactory.createAdmin("Admin User", adminAccount);

        Movie inception = adminFactory.createMovie("Inception", "Sci-fi Thriller", admin);
        inception.setLanguage("English");
        inception.setGenre("Sci-fi");
        
        Show show = adminFactory.createShow(inception, new Date(), screen1);
        
        // 3. Set Pricing Strategy for the Show
        Map<SeatType, Double> pricing = new HashMap<>();
        pricing.put(SeatType.REGULAR, 250.0);
        pricing.put(SeatType.PREMIUM, 400.0);
        show.setSeatPricing(pricing);

        System.out.println("Show created: " + show.getMovie().getTitle() + " with pricing set.");

        // 4. User Searching for Movie
        Catalog catalog = new Catalog();
        catalog.addMovie(inception);
        SearchStrategy search = new TitleSearchStrategy();
        List<Movie> results = search.search(catalog, "Inception");
        
        if (!results.isEmpty()) {
            System.out.println("User found movie: " + results.get(0).getTitle());
        }

        // 5. User Selecting Seats (Simulate Seat Locking)
        SeatBookingService bookingService = new SeatBookingService();
        List<ShowSeat> selectedSeats = new ArrayList<>();
        selectedSeats.add(screen1.getSeats().get(0)); // A1
        selectedSeats.add(screen1.getSeats().get(1)); // A2
        
        boolean reserved = bookingService.reserveSeats(show, selectedSeats);
        if (reserved) {
            System.out.println("Seats temporarily reserved. Proceeding to payment...");
            
            // 6. Create Booking
            BookingFactory bookingFactory = new RegularBookingFactory();
            Booking booking = bookingFactory.createBooking(show, selectedSeats);
            System.out.println("Booking created. Total Amount: " + booking.getTotalAmount());

            // 7. Payment
            PaymentStrategy payment = new CreditCardStrategy();
            boolean paid = booking.makePayment(payment);
            
            if (paid) {
                System.out.println("Payment successful. Booking Confirmed!");
                
                // 8. Notification
                SystemNotifier notifier = new SystemNotifier();
                Customer customer = new Customer("Alice", new Account());
                notifier.addObserver(customer);
                notifier.notifyObservers("Booking Confirmed for " + booking.getShow().getMovie().getTitle());
            } else {
                System.out.println("Payment failed. Seats released.");
                booking.cancel();
            }
        } else {
            System.out.println("Selected seats are not available.");
        }
        
        // 9. Demonstrate Concurrency (Simulated)
        // In a real app, this would be tested with threads.
        // We can show that trying to book the same seats again fails.
        System.out.println("\n--- Trying to double book same seats ---");
        boolean doubleBook = bookingService.reserveSeats(show, selectedSeats);
        System.out.println("Double booking result (should be false): " + doubleBook);
    }
}
