package org.example.strategy;

import java.util.concurrent.*;

public class SlidingWindowRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeMs;

    private static class ClientLog {
        final ConcurrentLinkedQueue<Long> timestamps = new ConcurrentLinkedQueue<>();
        long lastAccessedAt = System.currentTimeMillis();
    }

    private final ConcurrentHashMap<String, ClientLog> logs = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sliding-window-cleanup");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(
            this::evictStaleEntries, windowSizeMs, windowSizeMs, TimeUnit.MILLISECONDS
        );
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
    public void shutdown() {
        scheduler.shutdown();
    }
}
