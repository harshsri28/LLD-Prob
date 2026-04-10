package org.example.models;

import org.example.enums.AccountStatus;
import org.example.observer.EventObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User implements EventObserver {
    private String userId;
    private String username;
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private String email;
    private String password;
    private AccountStatus status;
    private LocalDateTime createdAt;

    // Thread-safe sets for followers/following (stores userIds, no duplicates, concurrent access)
    private Set<String> followers;
    private Set<String> following;

    // Thread-safe lists for user's own data
    private List<Long> tweetIds;
    private List<Notification> notifications;
    private List<Long> likedTweetIds;

    public User(String username, String displayName, String bio, String profilePictureUrl) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.followers = ConcurrentHashMap.newKeySet();
        this.following = ConcurrentHashMap.newKeySet();
        this.tweetIds = Collections.synchronizedList(new ArrayList<>());
        this.notifications = Collections.synchronizedList(new ArrayList<>());
        this.likedTweetIds = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void onEvent(String message) {
        System.out.println("[Notification for @" + username + "] " + message);
    }

    public void addFollower(String followerUserId) {
        followers.add(followerUserId);
    }

    public void removeFollower(String followerUserId) {
        followers.remove(followerUserId);
    }

    public void addFollowing(String followeeUserId) {
        following.add(followeeUserId);
    }

    public void removeFollowing(String followeeUserId) {
        following.remove(followeeUserId);
    }

    public void addTweetId(long tweetId) {
        tweetIds.add(tweetId);
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void addLikedTweetId(long tweetId) {
        likedTweetIds.add(tweetId);
    }

    public void removeLikedTweetId(long tweetId) {
        likedTweetIds.remove(Long.valueOf(tweetId));
    }

    public int getFollowerCount() {
        return followers.size();
    }

    public int getFollowingCount() {
        return following.size();
    }

    public boolean isCelebrity() {
        return followers.size() > 10000;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<String> getFollowers() { return followers; }
    public Set<String> getFollowing() { return following; }
    public List<Long> getTweetIds() { return new ArrayList<>(tweetIds); }
    public List<Notification> getNotifications() { return new ArrayList<>(notifications); }
    public List<Long> getLikedTweetIds() { return new ArrayList<>(likedTweetIds); }

    @Override
    public String toString() {
        return "User{@" + username + ", userId='" + userId + "', displayName='" + displayName + "', followers=" + followers.size() +
                ", following=" + following.size() + "}";
    }
}
