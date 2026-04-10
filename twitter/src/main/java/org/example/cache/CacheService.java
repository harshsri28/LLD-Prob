package org.example.cache;

import org.example.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cache service with TTL and eviction policy.
 *
 * Fixes Problem 6: CacheService Is Incorrect by Design
 * - Entries have TTL (time-to-live) and auto-expire
 * - Scheduled cleanup evicts stale entries
 * - ConcurrentHashMap for thread-safety
 *
 * In production: backed by Redis/Caffeine with LRU eviction.
 */
public class CacheService {
    private Map<String, CacheEntry<List<Tweet>>> tweetCache = new ConcurrentHashMap<>();
    private long defaultTtlMs;
    private ScheduledExecutorService cleanupExecutor;

    public CacheService(long defaultTtlMs) {
        this.defaultTtlMs = defaultTtlMs;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleanup");
            t.setDaemon(true);
            return t;
        });
        // Periodic cleanup every 30 seconds
        cleanupExecutor.scheduleAtFixedRate(this::evictExpired, 30, 30, TimeUnit.SECONDS);
    }

    public CacheService() {
        this(60000); // Default 60 second TTL
    }

    public void cacheTweets(String query, List<Tweet> tweets) {
        tweetCache.put(query.toLowerCase(), new CacheEntry<>(tweets, System.currentTimeMillis() + defaultTtlMs));
    }

    public List<Tweet> getCachedTweets(String query) {
        CacheEntry<List<Tweet>> entry = tweetCache.get(query.toLowerCase());
        if (entry == null || entry.isExpired()) {
            tweetCache.remove(query.toLowerCase());
            return null; // Cache miss
        }
        System.out.println("Cache hit for query: " + query);
        return entry.getValue();
    }

    public void invalidate(String query) {
        tweetCache.remove(query.toLowerCase());
    }

    public void invalidateAll() {
        tweetCache.clear();
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        tweetCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
    }

    public int size() {
        return tweetCache.size();
    }

    // Cache entry with TTL
    private static class CacheEntry<T> {
        private T value;
        private long expiresAt;

        public CacheEntry(T value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        public T getValue() { return value; }
        public boolean isExpired() { return System.currentTimeMillis() > expiresAt; }
    }
}
