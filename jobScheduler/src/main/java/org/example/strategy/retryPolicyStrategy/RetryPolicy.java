package org.example.strategy.retryPolicyStrategy;

public interface RetryPolicy {
    boolean shouldRetry(int attempt);
    long nextDelayMs(int attempt);
}
