package org.example.strategy;

public interface RateLimiter {
    boolean isAllowed(String clientId);
    void shutdown();
}
