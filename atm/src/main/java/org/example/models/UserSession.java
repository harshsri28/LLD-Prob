package org.example.models;

import org.example.strategy.accountStrategy.AccountStrategy;

public class UserSession {
    private String userId;
    private AccountStrategy account;
    private long createdAt;
    private long lastActivity;

    public UserSession(String userId, AccountStrategy account, long createdAt) {
        this.userId = userId;
        this.account = account;
        this.createdAt = createdAt;
        this.lastActivity = createdAt;
    }

    public boolean isValid(long timeoutMs, long currentTime) {
        return (currentTime - lastActivity) < timeoutMs;
    }

    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
    }

    public String getUserId() { return userId; }
    public AccountStrategy getAccount() { return account; }
    public long getCreatedAt() { return createdAt; }
    public long getLastActivity() { return lastActivity; }

    @Override
    public String toString() {
        return "UserSession{userId='" + userId + "'}";
    }
}
