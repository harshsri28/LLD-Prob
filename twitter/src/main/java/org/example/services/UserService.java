package org.example.services;

import org.example.enums.AccountStatus;
import org.example.enums.NotificationType;
import org.example.factory.UserFactory;
import org.example.models.User;
import org.example.observer.EventPublisher;
import org.example.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * User Service - owns user profiles, follow/unfollow graph.
 *
 * Fixes Problem 7: No Data Ownership
 * - UserService owns user data and social graph
 * - Follow/unfollow are atomic operations on thread-safe sets
 * - Notifications published via events, not direct calls
 */
public class UserService {
    private UserRepository userRepo;
    private RateLimiterService rateLimiter;
    private NotificationService notificationService;
    private EventPublisher eventPublisher;

    public UserService(UserRepository userRepo, RateLimiterService rateLimiter,
                       NotificationService notificationService, EventPublisher eventPublisher) {
        this.userRepo = userRepo;
        this.rateLimiter = rateLimiter;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    public User createUser(String username, String displayName, String bio, String profilePictureUrl) {
        if (userRepo.exists(username)) {
            throw new IllegalArgumentException("Username @" + username + " already exists");
        }
        User user = UserFactory.createUser(username, displayName, bio, profilePictureUrl);
        userRepo.addUser(user);
        eventPublisher.addObserver(user);
        System.out.println("User created: " + user);
        return user;
    }

    public User createUser(String username, String displayName) {
        return createUser(username, displayName, "", "");
    }

    public void followUser(String followerUsername, String followeeUsername) {
        if (followerUsername.equals(followeeUsername)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        if (rateLimiter.isRateLimited(followerUsername, "follow")) {
            throw new IllegalArgumentException("Follow action rate-limited for @" + followerUsername);
        }

        Optional<User> followerOpt = userRepo.getUserByUsername(followerUsername);
        Optional<User> followeeOpt = userRepo.getUserByUsername(followeeUsername);

        if (followerOpt.isEmpty() || followeeOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User follower = followerOpt.get();
        User followee = followeeOpt.get();

        // Thread-safe: ConcurrentHashMap.newKeySet().add() is atomic
        follower.addFollowing(followeeUsername);
        followee.addFollower(followerUsername);

        // Async notification via service (not blocking)
        notificationService.notifyUser(followeeUsername,
                "@" + followerUsername + " started following you.", NotificationType.FOLLOW);

        eventPublisher.publish(NotificationType.FOLLOW,
                "@" + followerUsername + " followed @" + followeeUsername);

        System.out.println("@" + followerUsername + " now follows @" + followeeUsername);
    }

    public void unfollowUser(String followerUsername, String followeeUsername) {
        Optional<User> followerOpt = userRepo.getUserByUsername(followerUsername);
        Optional<User> followeeOpt = userRepo.getUserByUsername(followeeUsername);

        if (followerOpt.isEmpty() || followeeOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User follower = followerOpt.get();
        User followee = followeeOpt.get();

        follower.removeFollowing(followeeUsername);
        followee.removeFollower(followerUsername);

        System.out.println("@" + followerUsername + " unfollowed @" + followeeUsername);
    }

    public Optional<User> getUser(String username) {
        return userRepo.getUserByUsername(username);
    }

    public List<User> searchUsers(String query) {
        return userRepo.searchByUsername(query);
    }

    public void deactivateUser(String username) {
        Optional<User> userOpt = userRepo.getUserByUsername(username);
        userOpt.ifPresent(user -> {
            user.setStatus(AccountStatus.DEACTIVATED);
            System.out.println("User @" + username + " deactivated");
        });
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }
}
