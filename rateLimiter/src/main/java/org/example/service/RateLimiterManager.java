package org.example.service;

import org.example.factory.RateLimiterFactory;
import org.example.strategy.RateLimiter;

public class RateLimiterManager {
    static  volatile RateLimiterManager instance;
    volatile RateLimiter rateLimiter;

    public RateLimiterManager(){
        rateLimiter = RateLimiterFactory.create("fixed", 10, 60_000);
    }

    public static RateLimiterManager getInstance(){
        if(instance == null){
            synchronized (RateLimiterManager.class){
                if(instance == null){
                    instance = new RateLimiterManager();
                }
            }
        }

        return instance;
    }

    public boolean allowedRequest(String clientId){
        return rateLimiter.allowedRequest(clientId);
    }

    public void updateRateLimiter(String type, int maxRequests, long windowSizeMs){
        rateLimiter = RateLimiterFactory.create(type, maxRequests, windowSizeMs);
    }
}
