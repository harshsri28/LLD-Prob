package org.example.strategy.retryPolicyStrategy;

public class ExponentialBackoffRetry implements RetryPolicy{
    int maxAttempts;
    long baseDelay;

    public ExponentialBackoffRetry(int maxAttempts, long baseDelay) {
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
    }

    @Override
    public boolean shouldRetry(int attempt) {
        return attempt < maxAttempts;
    }

    @Override
    public long nextDelayMs(int attempt) {
        return baseDelay * (1L << attempt);
    }
}
