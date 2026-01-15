package org.example.service;

import org.example.exceptions.PatientNotFoundException;
import org.example.models.Patient;
import org.example.repository.PatientRepository;

import java.util.UUID;

public class PatientService {
    PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public Patient register(String name) {
        Patient patient = new Patient(name);
        repo.save(patient);
        return patient;
    }

    public Patient findById(UUID id) {
        Patient patient = repo.findById(id);
        if(patient == null) {
            throw new PatientNotFoundException("Patient not found");
        }
        return patient;
    }
}
