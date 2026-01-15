package org.example.strategy;

import org.example.models.DoctorSlot;

import java.util.List;

public interface SlotRankStrategy {
    List<DoctorSlot> rank(List<DoctorSlot> slots) ;
}
