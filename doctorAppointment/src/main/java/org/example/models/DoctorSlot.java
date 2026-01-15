package org.example.models;

public class DoctorSlot {
    Doctor doctor;
    String slot;

    public DoctorSlot(Doctor doctor, String slot) {
        this.doctor = doctor;
        this.slot = slot;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}
