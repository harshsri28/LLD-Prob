package org.example.observer;

import org.example.enums.NotificationType;

/**
 * Represents an event in the Twitter system.
 * Events are published asynchronously and consumed by event listeners.
 * This decouples tweet creation from side-effects like timeline fanout,
 * hashtag indexing, mention notifications, and analytics.
 */
public class TwitterEvent {
    private NotificationType type;
    private String sourceUserId;
    private String targetUserId;
    private long tweetId;
    private String message;
    private long timestamp;

    public TwitterEvent(NotificationType type, String sourceUserId, String targetUserId, long tweetId, String message) {
        this.type = type;
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.tweetId = tweetId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public NotificationType getType() { return type; }
    public String getSourceUserId() { return sourceUserId; }
    public String getTargetUserId() { return targetUserId; }
    public long getTweetId() { return tweetId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "TwitterEvent{" + type + ", from=" + sourceUserId + ", tweet=" + tweetId + ", msg='" + message + "'}";
    }
}
