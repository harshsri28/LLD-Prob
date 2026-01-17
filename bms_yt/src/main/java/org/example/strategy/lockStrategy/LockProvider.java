package org.example.strategy.lockStrategy;

public interface LockProvider {
    boolean tryLock(String key, String userId, long ttlMS);
    void unlock(String key);
    boolean isLockedExpired(String key);
    boolean isLockedBy(String key, String userId);
}
