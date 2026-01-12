package org.example.strategy;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiter {
    int capacity;
    double refillRatePerMs;

    private static class Bucket {
        double tokens;
        long lastRefill;
    }

    ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = refillRatePerSecond / 1000.0;
    }

    @Override
    public boolean allowedRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        buckets.putIfAbsent(clientId, new Bucket());

        Bucket b = buckets.get(clientId);

        synchronized (b){
            long elapsedTime = currentTime - b.lastRefill;
            double refilled = elapsedTime * refillRatePerMs;
            b.tokens = Math.min(capacity, b.tokens + refilled);
            b.lastRefill = currentTime;

            if(b.tokens < 1) return false;

            b.tokens--;
            return true;
        }

    }
}
