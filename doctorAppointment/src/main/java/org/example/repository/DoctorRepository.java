package org.example.repository;

import org.example.enums.Specialization;
import org.example.models.Doctor;

import javax.print.Doc;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DoctorRepository {
    Map<UUID, Doctor> doctorsMap = new ConcurrentHashMap<>();

    public void save(Doctor doctor) {
        doctorsMap.put(doctor.getId(), doctor);
    }

    public Doctor findById(UUID id) {
        return doctorsMap.get(id);
    }

    public List<Doctor> findBySpecialization(Specialization specialization) {
        List<Doctor> result = new ArrayList<>();
        for(Doctor doctor : doctorsMap.values()) {
            if(doctor.getSpecialization() == specialization) {
                result.add(doctor);
            }
        }
        return result;
    }
}
