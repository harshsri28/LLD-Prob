package org.example.strategy;

import org.example.util.EvictionScheduler;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiter, Closeable {
    private final int maxRequests;
    private final long windowSizeMs;

    private static class Window {
        long startTime;
        int count;
        long lastAccessedAt;

        Window(long startTime) {
            this.startTime = startTime;
            this.count = 0;
            this.lastAccessedAt = startTime;
        }
    }

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    // EvictionScheduler owns the thread lifecycle; this class only owns rate-limiting logic (SRP)
    private final EvictionScheduler evictor;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.evictor = new EvictionScheduler(this::evictStaleEntries, windowSizeMs, "fixed-window-cleanup");
    }

    @Override
    public boolean isAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        Window window = windows.computeIfAbsent(clientId, k -> new Window(currentTime));

        synchronized (window) {
            window.lastAccessedAt = currentTime;
            if (currentTime - window.startTime >= windowSizeMs) {
                window.startTime = currentTime;
                window.count = 0;
            }
            if (window.count >= maxRequests) return false;
            window.count++;
            return true;
        }
    }

    private void evictStaleEntries() {
        long cutoff = System.currentTimeMillis() - 2 * windowSizeMs;
        windows.entrySet().removeIf(e -> {
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
