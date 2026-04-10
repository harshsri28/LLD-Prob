package org.example.models;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Comment {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private long id;
    private String authorUserId;
    private long tweetId;
    private String text;
    private LocalDateTime createdAt;
    private Set<String> likes;

    public Comment(String authorUserId, long tweetId, String text) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.authorUserId = authorUserId;
        this.tweetId = tweetId;
        this.text = text;
        this.createdAt = LocalDateTime.now();
        this.likes = ConcurrentHashMap.newKeySet();
    }

    public boolean addLike(String userId) {
        return likes.add(userId);
    }

    public boolean removeLike(String userId) {
        return likes.remove(userId);
    }

    public int getLikeCount() {
        return likes.size();
    }

    // Getters
    public long getId() { return id; }
    public String getAuthorUserId() { return authorUserId; }
    public long getTweetId() { return tweetId; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<String> getLikes() { return likes; }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", authorId=" + authorUserId + ": '" + text + "', likes=" + likes.size() + "}";
    }
}
