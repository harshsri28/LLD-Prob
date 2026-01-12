package org.example.strategy;

import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiter {
    int maxRequests;
    long windowSizeMs;

    private static class Window {
        long startTime;
        int count;

        Window(long startTime) {
            this.startTime = startTime;
            this.count = 0;
        }
    }

    ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public boolean allowedRequest(String clientId) {
        long currentTime = System.currentTimeMillis();

        windows.putIfAbsent(clientId, new Window(currentTime));
        Window window = windows.get(clientId);

        synchronized (window) {
            if (currentTime - window.startTime >= windowSizeMs) {
                window.startTime = currentTime;
                window.count = 0;
            }

            if (window.count >= maxRequests) {
                return false;
            }

            window.count++;
            return true;
        }
    }

}
