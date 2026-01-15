package org.example.repository;

import org.example.models.Booking;
import org.example.models.Doctor;
import org.example.models.Patient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BookingRepository {
    Map<UUID, Booking> bookingMap = new ConcurrentHashMap<>();
    Map<String, Queue<UUID>> waitList = new ConcurrentHashMap<>();

    public void save(Booking booking) {
        bookingMap.put(booking.getId(), booking);
    }

    public void delete(UUID id) {
        bookingMap.remove(id);
    }

    public Booking findById(UUID id) {
        return bookingMap.get(id);
    }

    public List<Booking> findByDoctor(UUID doctorId){
        List<Booking> result = new ArrayList<>();
        for(Booking booking : bookingMap.values()) {
            if(booking.getDoctorId().equals(doctorId.toString())) {
                result.add(booking);
            }
        }
        return result;
    }

    public List<Booking> findByPatient(UUID patientID){
        List<Booking> result = new ArrayList<>();
        for(Booking booking : bookingMap.values()) {
            if(booking.getPatientId().equals(patientID.toString())) {
                result.add(booking);
            }
        }
        return result;
    }

    public void addToWaitList(String doctorSlotKey, UUID patientId) {
        waitList.putIfAbsent(doctorSlotKey, new ConcurrentLinkedQueue<>());
        waitList.get(doctorSlotKey).add(patientId);
    }

    public UUID popFromWaitList(String doctorSlotKey) {
        Queue<UUID> queue = waitList.get(doctorSlotKey);
        return queue != null ? queue.poll() : null;
    }

}
