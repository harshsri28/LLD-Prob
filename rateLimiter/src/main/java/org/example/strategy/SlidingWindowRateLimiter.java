package org.example.strategy;

import java.util.concurrent.*;

public class SlidingWindowRateLimiter implements RateLimiter {
    int maxRequests;
    long windowSizeMs;

    ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> logs = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public boolean allowedRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        logs.putIfAbsent(clientId, new ConcurrentLinkedQueue<>());

        var q = logs.get(clientId);

        synchronized (q) {
            while (!q.isEmpty() && currentTime - q.peek() >= windowSizeMs) {
                q.poll();
            }

            if (q.size() >= maxRequests) return false;

            q.add(currentTime);
            return true;
        }
    }
}
