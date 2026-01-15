package org.example.strategy;

import org.example.models.DoctorSlot;
import org.example.util.Utils;

import java.util.Comparator;
import java.util.List;

public class StartTimeRankStrategy implements SlotRankStrategy {
    @Override
    public List<DoctorSlot> rank(List<DoctorSlot> slots){
        slots.sort(Comparator.comparing(slot -> Utils.convertStringToLocalTime(slot.getSlot())));
        return slots;
    }
}
