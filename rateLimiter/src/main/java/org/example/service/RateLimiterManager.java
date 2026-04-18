package org.example.service;

import org.example.factory.RateLimiterType;
import org.example.strategy.RateLimiter;

import java.io.Closeable;
import java.io.IOException;

public class RateLimiterManager {
    private static volatile RateLimiterManager instance;
    private volatile RateLimiter rateLimiter;

    // Constructor depends only on the RateLimiter abstraction, not on any factory or enum (DIP)
    private RateLimiterManager(RateLimiter initialLimiter) {
        this.rateLimiter = initialLimiter;
    }

    public static RateLimiterManager getInstance() {
        if (instance == null) {
            synchronized (RateLimiterManager.class) {
                if (instance == null) {
                    // Default config lives here at the composition root, not buried in the constructor
                    instance = new RateLimiterManager(RateLimiterType.FIXED.create(10, 60_000));
                }
            }
        }
        return instance;
    }

    public boolean isAllowed(String clientId) {
        return rateLimiter.isAllowed(clientId);
    }

    // Caller constructs the new limiter; manager only manages it — no factory dependency (DIP)
    public synchronized void updateRateLimiter(RateLimiter newLimiter) {
        RateLimiter old = rateLimiter;
        rateLimiter = newLimiter;
        if (old instanceof Closeable) {
            try {
                ((Closeable) old).close();
            } catch (IOException ignored) {}
        }
    }
}
