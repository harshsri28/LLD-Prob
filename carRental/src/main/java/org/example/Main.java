package org.example;

import org.example.enums.VehicleType;
import org.example.factory.VehicleFactory;
import org.example.models.Booking;
import org.example.models.Branch;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repository.BookingRepository;
import org.example.repository.BranchRepository;
import org.example.services.BookingService;
import org.example.services.PaymentProcessor;
import org.example.strategy.PricingStrategy.HourlyPricingStrategy;
import org.example.strategy.PricingStrategy.PricingStrategy;
import org.example.strategy.bookingStrategy.BookingStrategy;
import org.example.strategy.bookingStrategy.LeastBookedVehicleStrategy;
import org.example.strategy.paymentStrategy.CreditCardPayment;
import org.example.strategy.paymentStrategy.PaymentStrategy;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 1. Initialize Repositories
        BranchRepository branchRepo = new BranchRepository();
        BookingRepository bookingRepo = new BookingRepository();

        // 2. Initialize Strategies
        BookingStrategy bookingStrategy = new LeastBookedVehicleStrategy();
        PricingStrategy pricingStrategy = new HourlyPricingStrategy();
        PaymentStrategy paymentStrategy = new CreditCardPayment();

        // 3. Initialize Service
        BookingService bookingService = BookingService.getInstance(branchRepo, bookingRepo, bookingStrategy, pricingStrategy);
        PaymentProcessor paymentProcessor = new PaymentProcessor(paymentStrategy);

        // 4. Create Data: 1 Branch, ONLY 1 SEDAN
        Branch branch1 = new Branch("B1", "Bangalore");
        Vehicle sedan1 = VehicleFactory.getVehicle(VehicleType.SEDAN, "KA01-1234", 100.0, 10.0);
        
        branch1.addVehicle(sedan1);
        branchRepo.addBranch(branch1);

        System.out.println("Setup Complete: Branch B1 has 1 SEDAN available.");

        // 5. Create 2 Users
        User user1 = new User("U1", "Alice", "alice@example.com");
        User user2 = new User("U2", "Bob", "bob@example.com");

        // 6. Simulate Concurrent Booking using ExecutorService
        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        
        // Define tasks for Alice and Bob
        Runnable aliceTask = () -> bookVehicleTask(bookingService, user1, paymentStrategy, branch1, "Alice");
        Runnable bobTask = () -> bookVehicleTask(bookingService, user2, paymentStrategy, branch1, "Bob");

        // Submit tasks
        executor.submit(aliceTask);
        executor.submit(bobTask);

        // Shutdown and wait
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("Executor did not terminate in the specified time.");
            executor.shutdownNow();
        }
        
        System.out.println("\nFinal Booking Count: " + bookingRepo.getAllBookings().size());
        if (bookingRepo.getAllBookings().size() == 1) {
            System.out.println("Test Passed: Only 1 booking was created for the single vehicle.");
        } else {
            System.out.println("Test Failed: Incorrect number of bookings.");
        }
    }

    private static void bookVehicleTask(BookingService bookingService, User user, PaymentStrategy paymentStrategy, Branch branch, String userName) {
        System.out.println(userName + ": Attempting to book a SEDAN...");
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(24);

        Optional<Booking> bookingOptional = bookingService.bookVehicle(
                "B1",
                VehicleType.SEDAN,
                startTime,
                endTime,
                user,
                paymentStrategy,
                branch,
                branch,
                100.0
        );

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            System.out.println("✅ " + userName + ": Booking SUCCESS! ID: " + booking.getBookingId());
        } else {
            System.out.println("❌ " + userName + ": Booking FAILED! (Vehicle likely taken)");
        }
    }
}
