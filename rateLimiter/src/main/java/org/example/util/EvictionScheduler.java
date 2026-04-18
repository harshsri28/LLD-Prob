package org.example.util;

import java.io.Closeable;
import java.util.concurrent.*;

/**
 * Responsible solely for scheduling and running a periodic eviction task on a daemon thread.
 * Extracted from strategy classes so they only own rate-limiting logic (SRP).
 */
public class EvictionScheduler implements Closeable {
    private final ScheduledExecutorService scheduler;

    public EvictionScheduler(Runnable evictTask, long intervalMs, String threadName) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, threadName);
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(evictTask, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }
}
