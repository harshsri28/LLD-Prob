package org.example.strategy;

/**
 * Sole concern: decide whether a request from a client is allowed.
 * Lifecycle (shutdown) is a separate concern handled via java.io.Closeable
 * on concrete implementations — callers that only check isAllowed are not
 * forced to know about shutdown (ISP).
 */
public interface RateLimiter {
    boolean isAllowed(String clientId);
}
