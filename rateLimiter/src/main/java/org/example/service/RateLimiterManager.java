package org.example.service;

import org.example.factory.RateLimiterFactory;
import org.example.factory.RateLimiterType;
import org.example.strategy.RateLimiter;

public class RateLimiterManager {
    private static volatile RateLimiterManager instance;
    private volatile RateLimiter rateLimiter;

    private RateLimiterManager() {  // private — singleton cannot be bypassed
        rateLimiter = RateLimiterFactory.create(RateLimiterType.FIXED, 10, 60_000);
    }

    public static RateLimiterManager getInstance() {
        if (instance == null) {
            synchronized (RateLimiterManager.class) {
                if (instance == null) {
                    instance = new RateLimiterManager();
                }
            }
        }
        return instance;
    }

    public boolean isAllowed(String clientId) {
        return rateLimiter.isAllowed(clientId);
    }

    // synchronized — prevents two concurrent updates from silently overwriting each other
    public synchronized void updateRateLimiter(RateLimiterType type, int maxRequests, long windowSizeMs) {
        RateLimiter old = rateLimiter;
        rateLimiter = RateLimiterFactory.create(type, maxRequests, windowSizeMs);
        old.shutdown();  // release cleanup thread of the replaced limiter
    }
}
