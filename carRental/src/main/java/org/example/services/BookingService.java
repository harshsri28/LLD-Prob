package org.example.services;

import org.example.enums.BookingStatus;
import org.example.enums.VehicleStatus;
import org.example.enums.VehicleType;
import org.example.models.Booking;
import org.example.models.Branch;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repository.BookingRepository;
import org.example.repository.BranchRepository;
import org.example.strategy.PricingStrategy.PricingStrategy;
import org.example.strategy.bookingStrategy.BookingStrategy;
import org.example.strategy.paymentStrategy.PaymentStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

public class BookingService {
    static volatile BookingService instance;
    BranchRepository branchRepo;
    BookingRepository bookingRepo;

    BookingStrategy bookingStrategy;
    PricingStrategy pricingStrategy;

    private BookingService(BranchRepository branchRepo, BookingRepository bookingRepo, BookingStrategy bookingStrategy, PricingStrategy pricingStrategy) {
        this.branchRepo = branchRepo;
        this.bookingRepo = bookingRepo;
        this.bookingStrategy = bookingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public static BookingService getInstance(BranchRepository branchRepo, BookingRepository bookingRepo, BookingStrategy bookingStrategy, PricingStrategy pricingStrategy) {
        if (instance == null) {
            synchronized (BookingService.class) {
                if (instance == null) {
                    instance = new BookingService(branchRepo, bookingRepo, bookingStrategy, pricingStrategy);
                }
            }
        }
        return instance;
    }

    public Optional<Booking> bookVehicle(String branchId, VehicleType vehicleType, LocalDateTime startTime, LocalDateTime endTime,
                                         User user, PaymentStrategy paymentStrategy,Branch pickUpBranch,Branch dropOffBranch, double distanceKm) {

        Branch branch = branchRepo.getBranchById(branchId);
        if(branch==null){
            System.out.println("Branch not found for id "+branchId);
            return Optional.empty();
        }

        List<Vehicle> activeVehicles = branch.getVehiclesByType(vehicleType).stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .filter(v -> !v.getIsBooked().get())
                .collect(Collectors.toList());

        if (activeVehicles.isEmpty()) {
            System.out.println("No available vehicles of type " + vehicleType);
            return Optional.empty();
        }

        Vehicle vehicle = bookingStrategy.bookVehicle(activeVehicles);
        if (vehicle == null) {
            System.out.println("No available vehicles of type " + vehicleType);
            return Optional.empty();
        }

        double amount = pricingStrategy.calculatePrice(vehicle, startTime, endTime, distanceKm);

        Booking booking = Booking.builder()
                .user(user)
                .vehicle(vehicle)
                .amount(amount)
                .startTime(startTime)
                .endTime(endTime)
                .pickUpBranch(pickUpBranch)
                .dropOffBranch(dropOffBranch)
                .distanceKm(distanceKm)
                .build();

        PaymentProcessor paymentProcessor = new PaymentProcessor(paymentStrategy);
        if (!paymentProcessor.pay(booking)) {
            System.out.println("Payment failed for booking: " + booking.getBookingId());
            vehicle.getIsBooked().set(false);
            return Optional.empty();
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepo.addBooking(booking);

        vehicle.incrementBookingCount();
        vehicle.setStatus(VehicleStatus.BOOKED);

        System.out.println(booking);
        return Optional.of(booking);
    }

    public void returnVehicle(String bookingId) {
        Optional<Booking> booking = bookingRepo.getBookingById(bookingId);

        if (!booking.isPresent()) {
            System.out.println("Booking not found for id " + bookingId);
            return;
        }

        Booking bookingInfo = booking.get();

        if (bookingInfo.getStatus() != BookingStatus.CONFIRMED) {
            System.out.println("Vehicle is not confirmed for booking: " + bookingId);
            return;
        }

        bookingInfo.setStatus(BookingStatus.COMPLETED);
        bookingInfo.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        bookingInfo.getVehicle().getIsBooked().set(false);

        Branch dropBranch = bookingInfo.getDropOffBranch();
        dropBranch.addVehicle(bookingInfo.getVehicle());

        System.out.println("Vehicle returned to branch: " + dropBranch.getCity() + " for booking: " + bookingId);


    }

}
