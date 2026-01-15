package org.example.service;

import org.example.enums.Specialization;
import org.example.exceptions.DoctorNotFoundExceptions;
import org.example.models.Doctor;
import org.example.repository.DoctorRepository;

import javax.print.Doc;
import java.util.List;
import java.util.UUID;

public class DoctorService {
    DoctorRepository repo;

    public DoctorService(DoctorRepository repo) {
        this.repo = repo;
    }

    public Doctor register(String name, Specialization specialization, double rating) {
        Doctor doctor = new Doctor(name, specialization, rating);
        repo.save(doctor);
        return doctor;
    }

    public void declareAvailability(UUID doctorId, List<String> slots){
        Doctor doctor = repo.findById(doctorId);
        if(doctor == null) {
            throw new DoctorNotFoundExceptions("Doctor not found");
        }
        for(String slot : slots) {
            doctor.getAvailability().put(slot, true);
        }
    }
}
