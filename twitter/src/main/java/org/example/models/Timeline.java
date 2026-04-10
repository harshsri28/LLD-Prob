package org.example.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Timeline represents a user's home feed.
 * Uses ConcurrentSkipListSet for thread-safe, ordered tweet storage.
 * Tweets are ordered by creation time (newest first).
 */
public class Timeline {
    private String ownerUserId;

    // Thread-safe sorted set: tweets ordered by time (newest first)
    // Stores tweet IDs for fan-out on write
    private ConcurrentSkipListSet<TimelineEntry> entries;

    private static final int MAX_TIMELINE_SIZE = 1000;

    public Timeline(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        // Ordered by timestamp descending (newest first)
        this.entries = new ConcurrentSkipListSet<>(
                Comparator.comparing(TimelineEntry::getCreatedAtMillis).reversed()
                        .thenComparing(TimelineEntry::getTweetId)
        );
    }

    public void addTweet(long tweetId, long createdAtMillis) {
        entries.add(new TimelineEntry(tweetId, createdAtMillis));
        // Evict oldest entries if timeline exceeds max size
        while (entries.size() > MAX_TIMELINE_SIZE) {
            entries.pollLast();
        }
    }

    public void removeTweet(long tweetId) {
        entries.removeIf(entry -> entry.getTweetId() == tweetId);
    }

    /**
     * Get paginated timeline entries.
     * @param page 0-indexed page number
     * @param pageSize number of entries per page
     */
    public List<Long> getPage(int page, int pageSize) {
        List<Long> result = new ArrayList<>();
        int skip = page * pageSize;
        int count = 0;
        for (TimelineEntry entry : entries) {
            if (count >= skip && count < skip + pageSize) {
                result.add(entry.getTweetId());
            }
            count++;
            if (count >= skip + pageSize) break;
        }
        return result;
    }

    public List<Long> getAllTweetIds() {
        List<Long> result = new ArrayList<>();
        for (TimelineEntry entry : entries) {
            result.add(entry.getTweetId());
        }
        return result;
    }

    public int size() {
        return entries.size();
    }

    public String getOwnerUserId() { return ownerUserId; }

    // Inner class for timeline entries
    public static class TimelineEntry {
        private long tweetId;
        private long createdAtMillis;

        public TimelineEntry(long tweetId, long createdAtMillis) {
            this.tweetId = tweetId;
            this.createdAtMillis = createdAtMillis;
        }

        public long getTweetId() { return tweetId; }
        public long getCreatedAtMillis() { return createdAtMillis; }
    }
}
