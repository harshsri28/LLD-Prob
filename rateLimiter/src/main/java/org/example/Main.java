package org.example;

import org.example.factory.RateLimiterType;
import org.example.service.RateLimiterManager;

public class Main {
    public static void main(String[] args) {
        RateLimiterManager manager = RateLimiterManager.getInstance();

        for (int i = 0; i < 19; i++) {
            System.out.println(manager.isAllowed("client1"));
        }

        System.out.println("Switching to sliding window");
        // Caller creates the limiter; manager only manages it
        manager.updateRateLimiter(RateLimiterType.SLIDING.create(20, 60_000));

        for (int i = 0; i < 22; i++) {
            System.out.println(manager.isAllowed("client1"));
        }
    }
}
