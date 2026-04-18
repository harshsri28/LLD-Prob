package org.example.strategy;

import java.util.concurrent.*;

public class TokenBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final double refillRatePerMs;
    private static final long STALE_TTL_MS = 5 * 60_000L;

    private static class Bucket {
        double tokens;
        long lastRefill;
        long lastAccessedAt;

        Bucket(int capacity) {
            this.tokens = capacity;                      // start with a full bucket
            this.lastRefill = System.currentTimeMillis();
            this.lastAccessedAt = this.lastRefill;
        }
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;

    public TokenBucketRateLimiter(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = refillRatePerSecond / 1000.0;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "token-bucket-cleanup");
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
    public void shutdown() {
        scheduler.shutdown();
    }
}
