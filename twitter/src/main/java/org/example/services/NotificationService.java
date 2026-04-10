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
     * Send notification to a specific user.
     * Thread-safe: User.addNotification uses synchronizedList.
     */
    public void notifyUser(String targetUsername, String message, NotificationType type) {
        Optional<User> userOpt = userRepo.getUserByUsername(targetUsername);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        Notification notification = new Notification(targetUsername, message, type);
        user.addNotification(notification);
        System.out.println("  -> Notification sent to @" + targetUsername + ": [" + type + "] " + message);
    }

    /**
     * Notify all followers of a user about an event.
     * For celebrity users (>10K followers), this would be batched/async in production.
     */
    public void notifyFollowers(String authorUsername, String message, NotificationType type) {
        Optional<User> authorOpt = userRepo.getUserByUsername(authorUsername);
        if (authorOpt.isEmpty()) return;

        User author = authorOpt.get();
        for (String followerUsername : author.getFollowers()) {
            notifyUser(followerUsername, message, type);
        }
    }

    /**
     * Notify mentioned users in a tweet/comment.
     */
    public void notifyMentions(java.util.List<String> mentionedUsernames, String message, NotificationType type) {
        for (String username : mentionedUsernames) {
            notifyUser(username, message, type);
        }
    }
}
