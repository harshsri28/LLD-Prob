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
        if (userRepo.existsByUsername(username)) {
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

    public void followUser(String followerUserId, String followeeUserId) {
        if (followerUserId.equals(followeeUserId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        if (rateLimiter.isRateLimited(followerUserId, "follow")) {
            throw new IllegalArgumentException("Follow action rate-limited for " + followerUserId);
        }

        Optional<User> followerOpt = userRepo.getUserById(followerUserId);
        Optional<User> followeeOpt = userRepo.getUserById(followeeUserId);

        if (followerOpt.isEmpty() || followeeOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User follower = followerOpt.get();
        User followee = followeeOpt.get();

        // Thread-safe: ConcurrentHashMap.newKeySet().add() is atomic
        follower.addFollowing(followeeUserId);
        followee.addFollower(followerUserId);

        // Async notification via service (not blocking)
        notificationService.notifyUser(followeeUserId,
                "@" + follower.getUsername() + " started following you.", NotificationType.FOLLOW);

        eventPublisher.publish(NotificationType.FOLLOW,
                "@" + follower.getUsername() + " followed @" + followee.getUsername());

        System.out.println("@" + follower.getUsername() + " now follows @" + followee.getUsername());
    }

    public void unfollowUser(String followerUserId, String followeeUserId) {
        Optional<User> followerOpt = userRepo.getUserById(followerUserId);
        Optional<User> followeeOpt = userRepo.getUserById(followeeUserId);

        if (followerOpt.isEmpty() || followeeOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User follower = followerOpt.get();
        User followee = followeeOpt.get();

        follower.removeFollowing(followeeUserId);
        followee.removeFollower(followerUserId);

        System.out.println("@" + follower.getUsername() + " unfollowed @" + followee.getUsername());
    }

    public Optional<User> getUserById(String userId) {
        return userRepo.getUserById(userId);
    }

    public Optional<User> getUser(String username) {
        return userRepo.getUserByUsername(username);
    }

    public List<User> searchUsers(String query) {
        return userRepo.searchByUsername(query);
    }

    public void deactivateUser(String userId) {
        Optional<User> userOpt = userRepo.getUserById(userId);
        userOpt.ifPresent(user -> {
            user.setStatus(AccountStatus.DEACTIVATED);
            System.out.println("User @" + user.getUsername() + " deactivated");
        });
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }
}
