package org.example.strategy.schedulingStrategy;

import java.time.Instant;

public class CronStrategy implements SchedulingStrategy{
    int seconds;

    public CronStrategy(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public Instant nextExecutionTime(){
        return Instant.now().plusSeconds(seconds);
    }

    @Override
    public boolean isRecurring(){
        return true;
    }
}
