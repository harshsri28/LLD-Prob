package org.example.strategy.lockStrategy;

import java.util.concurrent.*;

public class InMemoryLockProvider implements LockProvider {
    public static class Expiry{
        long deadline;
        String owner;
        public Expiry(long deadline, String owner) {
            this.deadline = deadline;
            this.owner = owner;
        }
    }

    ConcurrentMap<String, Expiry> locks = new ConcurrentHashMap<>();
    ScheduledExecutorService sweeper = Executors.newSingleThreadScheduledExecutor();

    public InMemoryLockProvider(){
        sweeper.scheduleAtFixedRate(this::sweep,1,1, TimeUnit.MINUTES);
    }

    public void sweep(){
       long now = System.currentTimeMillis();
       locks.entrySet().removeIf(e -> e.getValue().deadline <= now);
    }

    @Override
    public boolean tryLock(String key, String userId, long ttlMS) {
        long now = System.currentTimeMillis();
        Expiry expiry =  new Expiry(now + ttlMS, userId);
        return locks.compute(key, (k, v) -> (v== null || v.deadline <= now) ? expiry : v) == expiry;
    }

    @Override
    public void unlock(String key) {
        locks.remove(key);
    }

    @Override
    public boolean isLockedBy(String key, String userId) {
        Expiry expiry = locks.get(key);
        return expiry != null && expiry.owner.equals(userId);
    }

    @Override
    public boolean isLockedExpired(String key) {
        Expiry expiry = locks.get(key);
        return expiry != null && expiry.deadline <= System.currentTimeMillis();
    }
}
