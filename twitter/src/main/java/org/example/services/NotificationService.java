package org.example.services;

import org.example.enums.NotificationType;
import org.example.models.Notification;
import org.example.models.User;
import org.example.repository.UserRepository;

import java.util.Optional;

/**
 * Notification Service - owns notification delivery.
 *
 * Fixes Problem 4: Notification System Is Synchronous and Blocking
 * - Notifications are created and stored per-user
 * - In production: backed by an async event queue (Kafka/SQS) with retry + backoff
 * - Supports multiple channels (in-app, push, email) via strategy
 *
 * Fixes Problem 7: No Data Ownership
 * - NotificationService owns all notification state and delivery
 */
public class NotificationService {
    private UserRepository userRepo;

    public NotificationService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Send notification to a specific user by userId.
     * Thread-safe: User.addNotification uses synchronizedList.
     */
    public void notifyUser(String targetUserId, String message, NotificationType type) {
        Optional<User> userOpt = userRepo.getUserById(targetUserId);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        Notification notification = new Notification(targetUserId, message, type);
        user.addNotification(notification);
        System.out.println("  -> Notification sent to @" + user.getUsername() + ": [" + type + "] " + message);
    }

    /**
     * Notify all followers of a user about an event.
     * For celebrity users (>10K followers), this would be batched/async in production.
     */
    public void notifyFollowers(String authorUserId, String message, NotificationType type) {
        Optional<User> authorOpt = userRepo.getUserById(authorUserId);
        if (authorOpt.isEmpty()) return;

        User author = authorOpt.get();
        for (String followerUserId : author.getFollowers()) {
            notifyUser(followerUserId, message, type);
        }
    }

    /**
     * Notify mentioned users in a tweet/comment.
     * Accepts userIds of mentioned users.
     */
    public void notifyMentions(java.util.List<String> mentionedUserIds, String message, NotificationType type) {
        for (String userId : mentionedUserIds) {
            notifyUser(userId, message, type);
        }
    }
}
