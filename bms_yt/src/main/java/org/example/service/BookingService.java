package org.example.service;

import org.example.enums.BookingStatus;
import org.example.enums.PaymentType;
import org.example.factory.PaymentStrategyFactory;
import org.example.models.Booking;
import org.example.models.Movie;
import org.example.models.Seat;
import org.example.models.Show;
import org.example.repository.BookingRepository;
import org.example.repository.MovieRepository;
import org.example.strategy.lockStrategy.LockProvider;
import org.example.strategy.payment.PaymentStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookingService {
    LockProvider lockProvider;
    BookingRepository bookingRepo;

    PaymentStrategyFactory paymentStrategyFactory ;

    static long TTL = 5000L;
    public BookingService(LockProvider lockProvider, BookingRepository bookingRepo) {
        this.lockProvider = lockProvider;
        this.bookingRepo = bookingRepo;
    }

    public Booking creatingBooking(String userId, Show show, List<String> seatIds) {
        // 1. Validate seats exist in the show
        List<Seat> showSeats = show.getSeats();
        Set<String> showSeatIds = showSeats.stream().map(Seat::getId).collect(Collectors.toSet());
        for (String seatId : seatIds) {
            if (!showSeatIds.contains(seatId)) {
                throw new RuntimeException("Seat " + seatId + " does not exist in this show");
            }
        }

        // 2. Check if seats are already CONFIRMED
        List<Booking> existingBookings = bookingRepo.getBookingsByShow(show.getId());
        Set<String> bookedSeats = new HashSet<>();
        for (Booking b : existingBookings) {
            if (b.getStatus() == BookingStatus.CONFIRMED) {
                bookedSeats.addAll(b.getSeatIds());
            }
        }

        for (String seatId : seatIds) {
            if (bookedSeats.contains(seatId)) {
                throw new RuntimeException("Seat " + seatId + " is already booked");
            }
        }

        // 3. Acquire Locks with Rollback
        List<String> lockedSeats = new ArrayList<>();
        try {
            for(String seatId : seatIds){
                String key = show.getId() + ":" + seatId;
                if(!lockProvider.tryLock(key, userId, TTL)){
                    throw new RuntimeException("Seat " + seatId + " is temporarily locked");
                }
                lockedSeats.add(seatId);
            }
        } catch (RuntimeException e) {
            // Rollback: Unlock already locked seats
            for (String seatId : lockedSeats) {
                String key = show.getId() + ":" + seatId;
                lockProvider.unlock(key);
            }
            throw e; // Re-throw
        }

        double totalPrice =0;

        for(Seat seat : show.getSeats()){
            if(seatIds.contains(seat.getId())){
                totalPrice += seat.getPrice();
            }
        }

        Booking booking = new Booking(
                UUID.randomUUID().toString(),
                show.getId(),
                userId,
                seatIds,
                BookingStatus.CREATED,
                null,
                totalPrice
        );

        bookingRepo.save(booking);
        return booking;
    }

    public void confirmBooking(Booking booking, PaymentType paymentType) {
        if(booking.getStatus() != BookingStatus.CREATED){
            throw new RuntimeException("Booking is not in created state, invalid state");
        }

        for(String seatId: booking.getSeatIds()){
            String key = booking.getShowId() + ":" + seatId;
            if(lockProvider.isLockedExpired(key) || !lockProvider.isLockedBy(key, booking.getUserId())){
                throw new RuntimeException("Seat is not locked by use now");
            }
        }

        booking.setPaymentType(paymentType);
        PaymentStrategy paymentStrategy = PaymentStrategyFactory.getPaymentStrategy(paymentType);
        paymentStrategy.pay(booking);

        for(String seatId: booking.getSeatIds()){
            String key = booking.getShowId() + ":" + seatId;
            lockProvider.unlock(key);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepo.save(booking);
        System.out.println("Booking confirmed: " + booking.getBookingId());
    }

}
