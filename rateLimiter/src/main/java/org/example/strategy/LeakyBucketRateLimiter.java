package org.example.strategy;

import org.example.util.EvictionScheduler;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketRateLimiter implements RateLimiter, Closeable {
    private final int capacity;
    private final double leakRatePerMs;
    private static final long STALE_TTL_MS = 5 * 60_000L;

    private static class Bucket {
        double water;
        long lastLeakTime;
        long lastAccessedAt;

        Bucket() {
            this.water = 0;
            this.lastLeakTime = System.currentTimeMillis();
            this.lastAccessedAt = this.lastLeakTime;
        }
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final EvictionScheduler evictor;

    public LeakyBucketRateLimiter(int capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerMs = leakRatePerSecond / 1000.0;
        this.evictor = new EvictionScheduler(this::evictStaleEntries, STALE_TTL_MS, "leaky-bucket-cleanup");
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
    public void close() {
        evictor.close();
    }
}
