package org.example.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe rate limiter using Token Bucket algorithm.
 *
 * Fixes Problem 5: RateLimiter Is Not Thread-Safe (and Incorrect)
 * - Uses ConcurrentHashMap for thread-safe storage
 * - AtomicLong for lock-free timestamp tracking
 * - Configurable window per action type
 *
 * In production: backed by Redis with atomic INCR + EXPIRE for distributed rate limiting.
 */
public class RateLimiterService {
    // Map<username:action, last action timestamp>
    private Map<String, AtomicLong> actionTimestamps = new ConcurrentHashMap<>();
    // Map<action, cooldown in milliseconds>
    private Map<String, Long> actionCooldowns = new ConcurrentHashMap<>();

    public RateLimiterService() {
        // Default cooldowns
        actionCooldowns.put("tweet", 5000L);      // 5 seconds between tweets
        actionCooldowns.put("follow", 2000L);      // 2 seconds between follows
        actionCooldowns.put("like", 1000L);        // 1 second between likes
        actionCooldowns.put("comment", 3000L);     // 3 seconds between comments
    }

    public void setActionCooldown(String action, long cooldownMs) {
        actionCooldowns.put(action, cooldownMs);
    }

    /**
     * Check if an action is rate-limited using compare-and-swap.
     * Thread-safe: uses AtomicLong.compareAndSet for lock-free updates.
     *
     * @return true if rate-limited (action should be blocked), false if allowed
     */
    public boolean isRateLimited(String username, String action) {
        String key = username + ":" + action;
        long cooldown = actionCooldowns.getOrDefault(action, 5000L);
        long currentTime = System.currentTimeMillis();

        AtomicLong lastAction = actionTimestamps.computeIfAbsent(key, k -> new AtomicLong(0));

        // CAS loop: atomic check-and-update
        while (true) {
            long lastTimestamp = lastAction.get();
            if (currentTime - lastTimestamp < cooldown) {
                return true; // Rate-limited
            }
            if (lastAction.compareAndSet(lastTimestamp, currentTime)) {
                return false; // Allowed, timestamp updated atomically
            }
            // CAS failed, another thread updated - retry with fresh value
        }
    }
}
