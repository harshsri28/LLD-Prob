package org.example.repository;

import org.example.models.UserSession;
import org.example.strategy.accountStrategy.AccountStrategy;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    private final long sessionTimeout; // in milliseconds

    public SessionManager(long sessionTimeoutMs) {
        this.sessionTimeout = sessionTimeoutMs;
    }

    public String createSession(String userId, AccountStrategy account) {
        String sessionId = UUID.randomUUID().toString();
        UserSession session = new UserSession(userId, account, System.currentTimeMillis());
        activeSessions.put(sessionId, session);
        System.out.println("Session created: " + sessionId + " for user: " + userId);
        return sessionId;
    }

    public Optional<UserSession> getSession(String sessionId) {
        UserSession session = activeSessions.get(sessionId);
        if (session == null) return Optional.empty();

        if (!session.isValid(sessionTimeout, System.currentTimeMillis())) {
            invalidateSession(sessionId);
            System.out.println("Session expired: " + sessionId);
            return Optional.empty();
        }

        session.updateActivity();
        return Optional.of(session);
    }

    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
        System.out.println("Session invalidated: " + sessionId);
    }

    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        activeSessions.entrySet().removeIf(entry ->
                !entry.getValue().isValid(sessionTimeout, now));
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }
}
