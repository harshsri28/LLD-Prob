package org.example.services;

import org.example.models.Tweet;
import org.example.models.User;
import org.example.repository.TimelineRepository;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;
import org.example.strategy.timelineStrategy.FanOutOnReadStrategy;
import org.example.strategy.timelineStrategy.FanOutOnWriteStrategy;
import org.example.strategy.timelineStrategy.TimelineStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Timeline Service - owns timeline ordering and generation.
 *
 * Fixes Problem 3: Timeline Generation Is Not Designed at All
 * - Implements both HomeTimeline and UserTimeline
 * - Uses hybrid strategy: Fan-out on write for normal users, fan-out on read for celebrities
 * - Supports pagination via page/pageSize parameters
 * - Timeline entries stored in ConcurrentSkipListSet (ordered, thread-safe)
 *
 * Fixes Problem 7: No Data Ownership
 * - TimelineService owns timeline data and ordering logic
 */
public class TimelineService {
    private UserRepository userRepo;
    private TweetRepository tweetRepo;
    private TimelineRepository timelineRepo;

    // Strategies for hybrid model
    private TimelineStrategy fanOutOnWriteStrategy;
    private TimelineStrategy fanOutOnReadStrategy;

    // Threshold: users with more followers than this use fan-out on read
    private static final int CELEBRITY_THRESHOLD = 10000;

    public TimelineService(UserRepository userRepo, TweetRepository tweetRepo, TimelineRepository timelineRepo) {
        this.userRepo = userRepo;
        this.tweetRepo = tweetRepo;
        this.timelineRepo = timelineRepo;
        this.fanOutOnWriteStrategy = new FanOutOnWriteStrategy();
        this.fanOutOnReadStrategy = new FanOutOnReadStrategy();
    }

    /**
     * Get home timeline for a user (paginated).
     * Uses fan-out on write for normal users (pre-computed timeline).
     * Falls back to fan-out on read if timeline is empty or user follows celebrities.
     */
    public List<Tweet> getHomeTimeline(String userId, int page, int pageSize) {
        // Default: use fan-out on write (pre-computed)
        List<Tweet> timeline = fanOutOnWriteStrategy.getTimeline(userId, page, pageSize,
                userRepo, tweetRepo, timelineRepo);

        // If timeline is empty (new user or following celebrities), fall back to fan-out on read
        if (timeline.isEmpty()) {
            timeline = fanOutOnReadStrategy.getTimeline(userId, page, pageSize,
                    userRepo, tweetRepo, timelineRepo);
        }

        return timeline;
    }

    /**
     * Get user timeline (tweets by a specific user).
     */
    public List<Tweet> getUserTimeline(String userId) {
        return tweetRepo.getTweetsByUser(userId);
    }

    /**
     * Called when a new tweet is created.
     * Uses hybrid strategy: fan-out on write for normal users, no-op for celebrities.
     */
    public void onNewTweet(Tweet tweet, User author) {
        if (author.isCelebrity()) {
            // Celebrity: fan-out on read (no pre-computation)
            fanOutOnReadStrategy.onNewTweet(tweet, author, userRepo, timelineRepo);
        } else {
            // Normal user: fan-out on write (push to followers' timelines)
            fanOutOnWriteStrategy.onNewTweet(tweet, author, userRepo, timelineRepo);
        }
    }

    /**
     * Backfill timeline when a user follows someone new.
     * Adds recent tweets from the followed user to the follower's timeline.
     */
    public void backfillTimeline(String followerUserId, String followedUserId) {
        List<Tweet> recentTweets = tweetRepo.getTweetsByUser(followedUserId);

        Optional<User> followedUserOpt = userRepo.getUserById(followedUserId);
        if (followedUserOpt.isEmpty() || followedUserOpt.get().isCelebrity()) {
            return; // Don't backfill for celebrities
        }

        User follower = userRepo.getUserById(followerUserId).orElse(null);
        User followed = followedUserOpt.get();

        for (Tweet tweet : recentTweets) {
            timelineRepo.getOrCreateTimeline(followerUserId)
                    .addTweet(tweet.getId(), tweet.getCreatedAt().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());
        }

        String followerName = follower != null ? follower.getUsername() : followerUserId;
        System.out.println("Backfilled " + recentTweets.size() + " tweets from @" + followed.getUsername() + " to @" + followerName + "'s timeline");
    }
}
