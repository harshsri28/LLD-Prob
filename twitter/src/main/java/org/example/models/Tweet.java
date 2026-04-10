package org.example.models;

import org.example.enums.TweetStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Tweet {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private long id;
    private String authorUserId;
    private String text;
    private LocalDateTime createdAt;
    private TweetStatus status;

    // Thread-safe set for likes (userIds who liked)
    private Set<String> likes;
    // Thread-safe list for comments
    private List<Comment> comments;
    // Thread-safe list for media
    private List<Media> media;
    // Extracted hashtags and mentions
    private List<String> hashtags;
    private List<String> mentions;
    // Atomic retweet count
    private AtomicInteger retweetCount;

    // Private constructor for Builder
    private Tweet(Builder builder) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.authorUserId = builder.authorUserId;
        this.text = builder.text;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.status = TweetStatus.ACTIVE;
        this.likes = ConcurrentHashMap.newKeySet();
        this.comments = Collections.synchronizedList(new ArrayList<>());
        this.media = builder.media != null ? Collections.synchronizedList(new ArrayList<>(builder.media)) : Collections.synchronizedList(new ArrayList<>());
        this.hashtags = builder.hashtags != null ? new ArrayList<>(builder.hashtags) : new ArrayList<>();
        this.mentions = builder.mentions != null ? new ArrayList<>(builder.mentions) : new ArrayList<>();
        this.retweetCount = new AtomicInteger(0);
    }

    // Thread-safe like/unlike using CAS-like semantics via ConcurrentHashMap.newKeySet()
    public boolean addLike(String userId) {
        return likes.add(userId);
    }

    public boolean removeLike(String userId) {
        return likes.remove(userId);
    }

    public int getLikeCount() {
        return likes.size();
    }

    public boolean isLikedBy(String userId) {
        return likes.contains(userId);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addMedia(Media mediaFile) {
        media.add(mediaFile);
    }

    public void incrementRetweetCount() {
        retweetCount.incrementAndGet();
    }

    public void delete() {
        this.status = TweetStatus.DELETED;
    }

    public void flag() {
        this.status = TweetStatus.FLAGGED;
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String authorUserId;
        private String text;
        private LocalDateTime createdAt;
        private List<Media> media;
        private List<String> hashtags;
        private List<String> mentions;

        public Builder authorUserId(String authorUserId) { this.authorUserId = authorUserId; return this; }
        public Builder text(String text) { this.text = text; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder media(List<Media> media) { this.media = media; return this; }
        public Builder hashtags(List<String> hashtags) { this.hashtags = hashtags; return this; }
        public Builder mentions(List<String> mentions) { this.mentions = mentions; return this; }

        public Tweet build() {
            if (authorUserId == null || text == null) {
                throw new IllegalArgumentException("Author and text are required");
            }
            return new Tweet(this);
        }
    }

    // Getters
    public long getId() { return id; }
    public String getAuthorUserId() { return authorUserId; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TweetStatus getStatus() { return status; }
    public Set<String> getLikes() { return likes; }
    public List<Comment> getComments() { return new ArrayList<>(comments); }
    public List<Media> getMedia() { return new ArrayList<>(media); }
    public List<String> getHashtags() { return hashtags; }
    public List<String> getMentions() { return mentions; }
    public int getRetweetCount() { return retweetCount.get(); }

    @Override
    public String toString() {
        return "Tweet{id=" + id + ", authorId=" + authorUserId + ": '" + text + "', likes=" + likes.size() +
                ", comments=" + comments.size() + ", retweets=" + retweetCount.get() + "}";
    }
}
