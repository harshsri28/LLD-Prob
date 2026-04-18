package org.example;

import org.example.factory.RateLimiterType;
import org.example.service.RateLimiterManager;

public class Main {
    public static void main(String[] args) {
        RateLimiterManager instance = RateLimiterManager.getInstance();

        for (int i = 0; i < 19; i++) {
            System.out.println(instance.isAllowed("client1"));
        }

        System.out.println("Switching to sliding window");
        instance.updateRateLimiter(RateLimiterType.SLIDING, 20, 60_000);

        for (int i = 0; i < 22; i++) {
            System.out.println(instance.isAllowed("client1"));
        }
    }
}
