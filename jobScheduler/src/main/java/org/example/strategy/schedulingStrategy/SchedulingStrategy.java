package org.example.strategy.schedulingStrategy;

import java.time.Instant;

public interface SchedulingStrategy {
    Instant nextExecutionTime();
    boolean isRecurring();
}
