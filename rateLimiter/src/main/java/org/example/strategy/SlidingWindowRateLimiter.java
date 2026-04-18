package org.example.strategy;

import org.example.util.EvictionScheduler;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SlidingWindowRateLimiter implements RateLimiter, Closeable {
    private final int maxRequests;
    private final long windowSizeMs;

    private static class ClientLog {
        final ConcurrentLinkedQueue<Long> timestamps = new ConcurrentLinkedQueue<>();
        long lastAccessedAt = System.currentTimeMillis();
    }

    private final ConcurrentHashMap<String, ClientLog> logs = new ConcurrentHashMap<>();
    private final EvictionScheduler evictor;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.evictor = new EvictionScheduler(this::evictStaleEntries, windowSizeMs, "sliding-window-cleanup");
    }

    @Override
    public boolean isAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        ClientLog log = logs.computeIfAbsent(clientId, k -> new ClientLog());

        synchronized (log) {
            log.lastAccessedAt = currentTime;
            while (!log.timestamps.isEmpty() && currentTime - log.timestamps.peek() >= windowSizeMs) {
                log.timestamps.poll();
            }
            if (log.timestamps.size() >= maxRequests) return false;
            log.timestamps.add(currentTime);
            return true;
        }
    }

    private void evictStaleEntries() {
        long cutoff = System.currentTimeMillis() - 2 * windowSizeMs;
        logs.entrySet().removeIf(e -> {
            synchronized (e.getValue()) {
                return e.getValue().lastAccessedAt < cutoff;
            }
        });
    }

    @Override
    public void close() {
        evictor.close();
    }
}
