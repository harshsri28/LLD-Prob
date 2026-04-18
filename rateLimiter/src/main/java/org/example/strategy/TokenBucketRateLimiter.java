package org.example.strategy;

import org.example.util.EvictionScheduler;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiter, Closeable {
    private final int capacity;
    private final double refillRatePerMs;
    private static final long STALE_TTL_MS = 5 * 60_000L;

    private static class Bucket {
        double tokens;
        long lastRefill;
        long lastAccessedAt;

        Bucket(int capacity) {
            this.tokens = capacity;
            this.lastRefill = System.currentTimeMillis();
            this.lastAccessedAt = this.lastRefill;
        }
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final EvictionScheduler evictor;

    public TokenBucketRateLimiter(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = refillRatePerSecond / 1000.0;
        this.evictor = new EvictionScheduler(this::evictStaleEntries, STALE_TTL_MS, "token-bucket-cleanup");
    }

    @Override
    public boolean isAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        Bucket b = buckets.computeIfAbsent(clientId, k -> new Bucket(capacity));

        synchronized (b) {
            b.lastAccessedAt = currentTime;
            long elapsedTime = currentTime - b.lastRefill;
            b.tokens = Math.min(capacity, b.tokens + elapsedTime * refillRatePerMs);
            b.lastRefill = currentTime;
            if (b.tokens < 1) return false;
            b.tokens--;
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
