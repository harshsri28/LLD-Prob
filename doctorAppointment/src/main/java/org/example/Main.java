package org.example;

import org.example.enums.Specialization;
import org.example.models.Booking;
import org.example.models.Doctor;
import org.example.models.DoctorSlot;
import org.example.models.Patient;
import org.example.repository.BookingRepository;
import org.example.repository.DoctorRepository;
import org.example.repository.PatientRepository;
import org.example.service.BookingService;
import org.example.service.DoctorService;
import org.example.service.PatientService;
import org.example.strategy.StartTimeRankStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Initialize Repositories
        DoctorRepository doctorRepository = new DoctorRepository();
        PatientRepository patientRepository = new PatientRepository();
        BookingRepository bookingRepository = new BookingRepository();

        // Initialize Services
        DoctorService doctorService = new DoctorService(doctorRepository);
        PatientService patientService = new PatientService(patientRepository);
        BookingService bookingService = new BookingService(bookingRepository, doctorRepository, patientRepository);

        System.out.println("=== Part 1: Basic Usage ===");
        
        // Register Doctor
        Doctor doctor = doctorService.register("Dr. Smith", Specialization.CARDIOLOGIST, 4.5);
        System.out.println("Doctor registered: " + doctor.getName());

        // Declare Availability (using unsorted time strings)
        List<String> slots = Arrays.asList("10:00", "09:30", "11:00", "09:00");
        doctorService.declareAvailability(doctor.getId(), slots);
        System.out.println("Doctor availability declared: " + slots);

        // Register Patient
        Patient patient = patientService.register("John Doe");
        System.out.println("Patient registered: " + patient.getName());

        // Search for slots using StartTimeRankStrategy
        System.out.println("\nSearching for slots (sorted by start time):");
        List<DoctorSlot> sortedSlots = bookingService.search(Specialization.CARDIOLOGIST, new StartTimeRankStrategy());
        
        for (DoctorSlot slot : sortedSlots) {
            System.out.println("Found slot: " + slot.getSlot() + " with " + slot.getDoctor().getName());
        }

        // Book a slot
        if (!sortedSlots.isEmpty()) {
            String slotToBook = sortedSlots.get(0).getSlot();
            System.out.println("\nBooking slot: " + slotToBook);
            Booking booking = bookingService.book(patient.getId(), doctor.getId(), slotToBook);
            System.out.println("Booking ID: " + booking.getId());
        }

        System.out.println("\n=== Part 2: Concurrency Demonstration ===");
        
        // Setup for concurrency test
        Doctor concurrentDoctor = doctorService.register("Dr. Concurrent", Specialization.DERMATOLOGIST, 5.0);
        String sharedSlot = "14:00";
        doctorService.declareAvailability(concurrentDoctor.getId(), Collections.singletonList(sharedSlot));
        
        Patient p1 = patientService.register("Alice");
        Patient p2 = patientService.register("Bob");
        Patient p3 = patientService.register("Charlie");

        System.out.println("Starting concurrent booking attempt for slot " + sharedSlot + " by 3 patients...");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable bookTask1 = () -> attemptBooking(bookingService, p1, concurrentDoctor, sharedSlot);
        Runnable bookTask2 = () -> attemptBooking(bookingService, p2, concurrentDoctor, sharedSlot);
        Runnable bookTask3 = () -> attemptBooking(bookingService, p3, concurrentDoctor, sharedSlot);

        executor.submit(bookTask1);
        executor.submit(bookTask2);
        executor.submit(bookTask3);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\nFinal State Verification:");
        boolean isAvailable = concurrentDoctor.getAvailability().get(sharedSlot);
        System.out.println("Slot " + sharedSlot + " availability: " + isAvailable);
        
        long bookingCount = bookingRepository.findByDoctor(concurrentDoctor.getId()).stream()
                .filter(b -> b.getSlot().equals(sharedSlot))
                .count();
        System.out.println("Total confirmed bookings for slot " + sharedSlot + ": " + bookingCount);
        
        // Check waitlist (optional, depends on implementation visibility, but we can infer from output)
    }

    private static void attemptBooking(BookingService bookingService, Patient patient, Doctor doctor, String slot) {
        try {
            System.out.println(patient.getName() + " attempting to book...");
            bookingService.book(patient.getId(), doctor.getId(), slot);
            System.out.println("SUCCESS: " + patient.getName() + " booked the slot.");
        } catch (Exception e) {
            System.out.println("FAILED: " + patient.getName() + " failed to book. Reason: " + e.getMessage());
        }
    }
}
