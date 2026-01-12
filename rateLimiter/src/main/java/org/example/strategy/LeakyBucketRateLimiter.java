package org.example.strategy;

import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketRateLimiter implements RateLimiter {
    int capacity;
    double leakRatePerMs;

    private static class Bucket {
        double water;
        long lastLeakTime;
    }

    ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LeakyBucketRateLimiter(int capacity, double leakRatePerMs) {
        this.capacity = capacity;
        this.leakRatePerMs = leakRatePerMs/1000.0;
    }

    @Override
    public boolean allowedRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        buckets.putIfAbsent(clientId, new Bucket());

        Bucket b = buckets.get(clientId);

        synchronized (b){
            long elapsedTime = currentTime - b.lastLeakTime;
            double leaked = elapsedTime * leakRatePerMs;
            b.water = Math.max(0, b.water  - leaked);
            b.lastLeakTime = currentTime;

            if(b.water +1 > capacity) return false;

            b.water++;
            return true;
        }
    }
}
