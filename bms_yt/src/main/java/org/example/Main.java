package org.example;

import org.example.enums.PaymentType;
import org.example.models.*;
import org.example.repository.*;
import org.example.service.*;
import org.example.strategy.lockStrategy.InMemoryLockProvider;
import org.example.strategy.lockStrategy.LockProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Repositories
        BookingRepository bookingRepo = new BookingRepository();
        MovieRepository movieRepo = new MovieRepository();
        ShowRepository showRepo = new ShowRepository();
        TheatreRepository theatreRepo = new TheatreRepository();

        // 2. Initialize Lock Provider
        LockProvider lockProvider = new InMemoryLockProvider();

        // 3. Initialize Services
        MovieService movieService = new MovieService(movieRepo);
        TheatreService theatreService = new TheatreService(theatreRepo);
        ShowService showService = new ShowService(showRepo, theatreRepo, movieRepo);
        BookingService bookingService = new BookingService(lockProvider, bookingRepo);

        // 4. Setup Data
        // Create Movie
        Movie avengers = movieService.createMovie("movie1", "Avengers", 120);
        System.out.println("Movie created: " + avengers.getTitle());

        // Create Theatre
        Theatre pvr = theatreService.createTheatre("theatre1", "PVR Koramangala");
        System.out.println("Theatre created: " + pvr.getName());

        // Create Screen
        Screen screen1 = new Screen("screen1");
        pvr.addScreen(screen1);
        System.out.println("Screen created: " + screen1.getId());

        // Add Seats to Screen
        for (int i = 1; i <= 10; i++) {
            Seat seat = new RegularSeat(String.valueOf(i), 100.0);
            screen1.addSeats(seat);
        }
        System.out.println("10 Seats added to screen");

        // Create Show
        Show show1 = showService.createShow("show1", pvr.getId(), screen1.getId(), avengers.getId(), new Date());
        System.out.println("Show created: " + show1.getId());

        // 5. Simulate Multiple Users
        Runnable user1Task = () -> {
            String userId = "User1";
            List<String> seats = Arrays.asList("1", "2");
            try {
                System.out.println(userId + " attempting to book seats: " + seats);
                Booking booking = bookingService.creatingBooking(userId, show1, seats);
                System.out.println(userId + " created booking: " + booking.getBookingId());
                
                // Simulate payment processing time
                Thread.sleep(1000);
                
                bookingService.confirmBooking(booking, PaymentType.CREDIT_CARD);
                System.out.println(userId + " confirmed booking successfully.");
            } catch (Exception e) {
                System.out.println(userId + " failed to book: " + e.getMessage());
            }
        };

        Runnable user2Task = () -> {
            String userId = "User2";
            List<String> seats = Arrays.asList("2", "3");
            try {
                // Wait a bit to ensure User1 starts first (optional, but good for testing conflict)
                Thread.sleep(100); 
                System.out.println(userId + " attempting to book seats: " + seats);
                Booking booking = bookingService.creatingBooking(userId, show1, seats);
                System.out.println(userId + " created booking: " + booking.getBookingId());
                
                bookingService.confirmBooking(booking, PaymentType.UPI);
                System.out.println(userId + " confirmed booking successfully.");
            } catch (Exception e) {
                System.out.println(userId + " failed to book: " + e.getMessage());
            }
        };

        Thread t1 = new Thread(user1Task);
        Thread t2 = new Thread(user2Task);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // User 3 attempts to book same seats after User 1 confirmed
        System.out.println("\n--- Starting User 3 Test (Double Booking Check) ---");
        String userId3 = "User3";
        List<String> seats3 = Arrays.asList("1");
        try {
            System.out.println(userId3 + " attempting to book seats: " + seats3);
            Booking booking = bookingService.creatingBooking(userId3, show1, seats3);
            System.out.println(userId3 + " created booking: " + booking.getBookingId());
        } catch (Exception e) {
            System.out.println(userId3 + " failed to book: " + e.getMessage());
        }
        
        System.out.println("Simulation ended.");
        // Force exit because InMemoryLockProvider has a scheduled executor that prevents JVM exit
        System.exit(0);
    }
}
