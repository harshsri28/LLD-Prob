package org.example.repository;

import org.example.models.Patient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PatientRepository {
    Map<UUID, Patient> patientMap = new ConcurrentHashMap<>();

    public void save(Patient patient) {
        patientMap.put(patient.getId(), patient);
    }

    public Patient findById(UUID id) {
        return patientMap.get(id);
    }
}
