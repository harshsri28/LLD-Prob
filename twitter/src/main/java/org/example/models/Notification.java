package org.example.models;

import org.example.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Notification {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private long id;
    private String targetUserId;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
    private volatile boolean read;

    public Notification(String targetUserId, String message, NotificationType type) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.targetUserId = targetUserId;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    // Getters
    public long getId() { return id; }
    public String getTargetUserId() { return targetUserId; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }

    @Override
    public String toString() {
        return "Notification{[" + type + "] " + message + ", read=" + read + "}";
    }
}
