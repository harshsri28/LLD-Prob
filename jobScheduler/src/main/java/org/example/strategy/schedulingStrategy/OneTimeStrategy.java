package org.example.strategy.schedulingStrategy;

import java.time.Instant;

public class OneTimeStrategy implements SchedulingStrategy {
    boolean executed = false;

    @Override
    public Instant nextExecutionTime(){
        if(executed) return null;
        executed= true;
        return Instant.now();
    }

    @Override
    public boolean isRecurring(){
        return false;
    }
}
