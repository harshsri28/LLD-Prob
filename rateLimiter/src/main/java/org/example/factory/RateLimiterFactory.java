package org.example.factory;

import org.example.strategy.RateLimiter;

/**
 * Factory delegates entirely to the enum — no switch statement here.
 * Adding a new type requires only a new enum constant (OCP).
 */
public class RateLimiterFactory {
    private RateLimiterFactory() {}

    public static RateLimiter create(RateLimiterType type, int maxRequests, long windowSizeMs) {
        return type.create(maxRequests, windowSizeMs);
    }
}
