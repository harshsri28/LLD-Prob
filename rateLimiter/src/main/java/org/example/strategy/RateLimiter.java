package org.example.strategy;

public interface RateLimiter {
    boolean allowedRequest(String clientId);
}
