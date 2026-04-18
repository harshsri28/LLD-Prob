package org.example.factory;

import org.example.strategy.*;

public class RateLimiterFactory {
    private RateLimiterFactory() {}

    public static RateLimiter create(RateLimiterType type, int maxRequests, long windowSizeMs) {
        switch (type) {
            case FIXED:
                return new FixedWindowRateLimiter(maxRequests, windowSizeMs);
            case SLIDING:
                return new SlidingWindowRateLimiter(maxRequests, windowSizeMs);
            case TOKEN:
                // windowSizeMs is treated as refillRatePerSecond for token bucket
                return new TokenBucketRateLimiter(maxRequests, windowSizeMs);
            case LEAKY:
                // windowSizeMs is treated as leakRatePerSecond for leaky bucket
                return new LeakyBucketRateLimiter(maxRequests, windowSizeMs);
            default:
                throw new IllegalArgumentException("Unknown rate limiter type: " + type);
        }
    }
}
