package org.example.service;

import org.example.enums.Specialization;
import org.example.exceptions.BookingNotFoundException;
import org.example.models.Booking;
import org.example.models.Doctor;
import org.example.models.DoctorSlot;
import org.example.repository.BookingRepository;
import org.example.repository.DoctorRepository;
import org.example.repository.PatientRepository;
import org.example.strategy.SlotRankStrategy;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingService {
    BookingRepository bookingRepo;
    DoctorRepository doctorRepo;
    PatientRepository patientRepo;
    public BookingService(BookingRepository bookingRepo, DoctorRepository doctorRepo, PatientRepository patientRepo) {
        this.bookingRepo = bookingRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    public List<DoctorSlot> search(Specialization specialization, SlotRankStrategy strategy) {
        List<Doctor> doctors = doctorRepo.findBySpecialization(specialization);
        List<DoctorSlot> slots = new ArrayList<>();

        for(Doctor doctor : doctors) {
            for(Map.Entry<String, Boolean> e: doctor.getAvailability().entrySet()) {
                if(e.getValue()) {
                    slots.add(new DoctorSlot(doctor, e.getKey()));
                }
            }
        }

        return strategy.rank(slots);
    }

    public synchronized Booking book(UUID patientId, UUID doctorId, String slot) {
        Doctor doctor = doctorRepo.findById(doctorId);

        Map<String, Boolean> availability = doctor.getAvailability();

        if(!availability.containsKey(slot)) {
            throw new RuntimeException("Slot not available");
        }

        for(Booking booking : bookingRepo.findByPatient(patientId)) {
            if (booking.getSlot().equals(slot)) {
                throw new RuntimeException("Slot already booked by patient at a time");
            }
        }

        if(availability.get(slot)) {
            Booking booking = new Booking(patientId.toString(), doctorId.toString(), slot);
            bookingRepo.save(booking);
            availability.put(slot, false);

            System.out.println("\n" + "Booking done\n" + "Patient ID: " + patientId + "\n" + "Doctor ID: " + doctorId + "\n" + "Slot: " + slot);
            return booking;
        }else{
            String key = doctorId.toString() + "-" +slot;
            bookingRepo.addToWaitList(key, patientId);
            throw new RuntimeException("Slot a;ready booked , added to waitList");
        }
    }

    public synchronized void cancel(UUID bookingId) {
        Booking booking = bookingRepo.findById(bookingId);

        if(booking == null) {
            throw new BookingNotFoundException("Booking not found");
        }

        Doctor doctor = doctorRepo.findById(UUID.fromString(booking.getDoctorId()));
        doctor.getAvailability().put(booking.getSlot(), true);
        bookingRepo.delete(bookingId);

        System.out.println("\n" + "Booking cancelled\n" + "Patient ID: " + booking.getPatientId() + "\n" + "Doctor ID: " + booking.getDoctorId() + "\n" + "Slot: " + booking.getSlot());

        //promote the patient from waitlist
        String key = booking.getDoctorId() + "-" + booking.getSlot();
        UUID patientId = bookingRepo.popFromWaitList(key);
        if(patientId != null) {
            book(patientId, UUID.fromString(booking.getDoctorId()), booking.getSlot());
        }
    }

    public List<Booking> viewBookingByDoctor(UUID doctorId) {
        return bookingRepo.findByDoctor(doctorId);
    }
}
