package org.example.strategy;

import java.util.concurrent.*;

public class FixedWindowRateLimiter implements RateLimiter {
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
    private final ScheduledExecutorService scheduler;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "fixed-window-cleanup");
            t.setDaemon(true);
            return t;
        });
        // Evict entries not accessed for 2 full windows
        scheduler.scheduleAtFixedRate(
            this::evictStaleEntries, windowSizeMs, windowSizeMs, TimeUnit.MILLISECONDS
        );
    }

    @Override
    public boolean isAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        // computeIfAbsent is atomic — no separate putIfAbsent + get race
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
    public void shutdown() {
        scheduler.shutdown();
    }
}
