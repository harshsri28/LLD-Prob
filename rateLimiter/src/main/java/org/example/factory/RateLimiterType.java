package org.example.factory;

import org.example.strategy.*;

/**
 * Each enum constant owns its own creation logic (OCP).
 * Adding a new algorithm = add a new constant here + a new class.
 * RateLimiterFactory never needs to change.
 */
public enum RateLimiterType {
    FIXED {
        @Override
        public RateLimiter create(int maxRequests, long windowSizeMs) {
            return new FixedWindowRateLimiter(maxRequests, windowSizeMs);
        }
    },
    SLIDING {
        @Override
        public RateLimiter create(int maxRequests, long windowSizeMs) {
            return new SlidingWindowRateLimiter(maxRequests, windowSizeMs);
        }
    },
    TOKEN {
        @Override
        public RateLimiter create(int maxRequests, long windowSizeMs) {
            return new TokenBucketRateLimiter(maxRequests, windowSizeMs);
        }
    },
    LEAKY {
        @Override
        public RateLimiter create(int maxRequests, long windowSizeMs) {
            return new LeakyBucketRateLimiter(maxRequests, windowSizeMs);
        }
    };

    public abstract RateLimiter create(int maxRequests, long windowSizeMs);
}
