package org.example.strategy;

import java.util.concurrent.*;

public class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final double leakRatePerMs;
    private static final long STALE_TTL_MS = 5 * 60_000L;

    private static class Bucket {
        double water;
        long lastLeakTime;
        long lastAccessedAt;

        Bucket() {
            this.water = 0;
            this.lastLeakTime = System.currentTimeMillis();  // start from now, not epoch
            this.lastAccessedAt = this.lastLeakTime;
        }
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;

    // Parameter is leakRatePerSecond (consistent with TokenBucketRateLimiter)
    public LeakyBucketRateLimiter(int capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerMs = leakRatePerSecond / 1000.0;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "leaky-bucket-cleanup");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(
            this::evictStaleEntries, STALE_TTL_MS, STALE_TTL_MS, TimeUnit.MILLISECONDS
        );
    }

    @Override
    public boolean isAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        Bucket b = buckets.computeIfAbsent(clientId, k -> new Bucket());

        synchronized (b) {
            b.lastAccessedAt = currentTime;
            long elapsedTime = currentTime - b.lastLeakTime;
            b.water = Math.max(0, b.water - elapsedTime * leakRatePerMs);
            b.lastLeakTime = currentTime;

            if (b.water + 1 > capacity) return false;
            b.water++;
            return true;
        }
    }

    private void evictStaleEntries() {
        long cutoff = System.currentTimeMillis() - STALE_TTL_MS;
        buckets.entrySet().removeIf(e -> {
            synchronized (e.getValue()) {
                return e.getValue().lastAccessedAt < cutoff;
            }
        });
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }
}
