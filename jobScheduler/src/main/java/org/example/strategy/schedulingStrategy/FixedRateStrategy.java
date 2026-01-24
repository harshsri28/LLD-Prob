package org.example.strategy.schedulingStrategy;

import java.time.Duration;
import java.time.Instant;

public class FixedRateStrategy implements SchedulingStrategy {
    Duration interval;

    public FixedRateStrategy(Duration interval) {
        this.interval = interval;
    }

    @Override
    public Instant nextExecutionTime(){
        return Instant.now().plus(interval);
    }

    @Override
    public boolean isRecurring(){
        return true;
    }
}
