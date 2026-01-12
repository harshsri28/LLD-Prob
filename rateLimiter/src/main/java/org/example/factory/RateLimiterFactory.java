package org.example.factory;

import org.example.strategy.*;

public class RateLimiterFactory {
    public  static RateLimiter create(String type, int maxRequests, long windowSizeMs){
        switch (type.toLowerCase()){
            case "sliding":
                return new SlidingWindowRateLimiter(maxRequests, windowSizeMs);
            case "fixed":
                return new FixedWindowRateLimiter(maxRequests, windowSizeMs);
            case "leaky":
                return new LeakyBucketRateLimiter(maxRequests, maxRequests);
            case "token":
                return new TokenBucketRateLimiter(maxRequests, maxRequests);
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }
}
