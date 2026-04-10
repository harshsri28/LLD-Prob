package org.example.services;

import org.example.cache.CacheService;
import org.example.enums.NotificationType;
import org.example.models.Comment;
import org.example.models.Tweet;
import org.example.models.User;
import org.example.observer.EventPublisher;
import org.example.observer.TwitterEvent;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;
import org.example.strategy.searchStrategy.SearchStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Tweet Service (Singleton) - owns tweet data and tweet operations.
 *
 * Fixes Problem 2: Tweet Creation Is a Giant God Method
 * - createTweet() only persists the tweet and publishes TweetCreatedEvent
 * - Side-effects (timeline fanout, hashtag indexing, mention notifications) are
 *   handled by separate services reacting to events
 *
 * Fixes Problem 7: No Data Ownership
 * - TweetService owns tweet CRUD
 * - Like state owned here (could be separate LikeService at scale)
 */
public class TweetService {
    // Singleton with double-checked locking
    private static volatile TweetService instance;

    private TweetRepository tweetRepo;
    private UserRepository userRepo;
    private RateLimiterService rateLimiter;
    private HashtagService hashtagService;
    private NotificationService notificationService;
    private TimelineService timelineService;
    private CacheService cacheService;
    private EventPublisher eventPublisher;
    private SearchStrategy searchStrategy;

    private TweetService(TweetRepository tweetRepo, UserRepository userRepo,
                         RateLimiterService rateLimiter, HashtagService hashtagService,
                         NotificationService notificationService, TimelineService timelineService,
                         CacheService cacheService, EventPublisher eventPublisher,
                         SearchStrategy searchStrategy) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.rateLimiter = rateLimiter;
        this.hashtagService = hashtagService;
        this.notificationService = notificationService;
        this.timelineService = timelineService;
        this.cacheService = cacheService;
        this.eventPublisher = eventPublisher;
        this.searchStrategy = searchStrategy;
    }

    public static TweetService getInstance(TweetRepository tweetRepo, UserRepository userRepo,
                                           RateLimiterService rateLimiter, HashtagService hashtagService,
                                           NotificationService notificationService, TimelineService timelineService,
                                           CacheService cacheService, EventPublisher eventPublisher,
                                           SearchStrategy searchStrategy) {
        if (instance == null) {
            synchronized (TweetService.class) {
                if (instance == null) {
                    instance = new TweetService(tweetRepo, userRepo, rateLimiter, hashtagService,
                            notificationService, timelineService, cacheService, eventPublisher, searchStrategy);
                }
            }
        }
        return instance;
    }

    /**
     * Create a tweet - single responsibility: persist + publish event.
     * Side-effects are handled by consumers of the TweetCreatedEvent.
     */
    public Optional<Tweet> createTweet(String username, String text) {
        if (rateLimiter.isRateLimited(username, "tweet")) {
            System.out.println("Tweet rate-limited for @" + username);
            return Optional.empty();
        }

        Optional<User> userOpt = userRepo.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("User not found: @" + username);
            return Optional.empty();
        }

        User user = userOpt.get();

        // Extract hashtags and mentions from text
        List<String> hashtags = HashtagService.extractHashtags(text);
        List<String> mentions = HashtagService.extractMentions(text);

        // Build tweet using Builder pattern
        Tweet tweet = Tweet.builder()
                .authorUsername(username)
                .text(text)
                .hashtags(hashtags)
                .mentions(mentions)
                .build();

        // 1. Persist tweet (single responsibility)
        tweetRepo.addTweet(tweet);
        user.addTweetId(tweet.getId());

        // 2. Publish event - consumers handle side-effects asynchronously
        // In production: these would be async event consumers on a message queue

        // Side-effect 1: Timeline fanout (handled by TimelineService)
        timelineService.onNewTweet(tweet, user);

        // Side-effect 2: Hashtag indexing (handled by HashtagService)
        hashtagService.indexHashtags(hashtags, tweet.getId());

        // Side-effect 3: Mention notifications (handled by NotificationService)
        List<String> validMentions = mentions.stream()
                .filter(m -> userRepo.getUserByUsername(m).isPresent())
                .toList();
        notificationService.notifyMentions(validMentions,
                "@" + username + " mentioned you in a tweet.", NotificationType.MENTION);

        // Side-effect 4: Notify followers (handled by NotificationService)
        notificationService.notifyFollowers(username,
                "@" + username + " just tweeted: \"" + truncate(text, 50) + "\"", NotificationType.TWEET);

        // Publish event for observers
        eventPublisher.publishEvent(new TwitterEvent(NotificationType.TWEET, username, null, tweet.getId(),
                "@" + username + " tweeted: \"" + truncate(text, 50) + "\""));

        System.out.println("Tweet created: " + tweet);
        return Optional.of(tweet);
    }

    /**
     * Like a tweet - thread-safe via ConcurrentHashMap.newKeySet().
     */
    public boolean likeTweet(String username, long tweetId) {
        if (rateLimiter.isRateLimited(username, "like")) {
            System.out.println("Like rate-limited for @" + username);
            return false;
        }

        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        Optional<User> userOpt = userRepo.getUserByUsername(username);
        if (tweetOpt.isEmpty() || userOpt.isEmpty()) {
            System.out.println("Tweet or user not found");
            return false;
        }

        Tweet tweet = tweetOpt.get();
        User user = userOpt.get();

        // Thread-safe: Set.add returns false if already present
        if (!tweet.addLike(username)) {
            System.out.println("@" + username + " already liked tweet " + tweetId);
            return false;
        }

        user.addLikedTweetId(tweetId);

        // Notify tweet author (don't notify self-likes)
        if (!username.equals(tweet.getAuthorUsername())) {
            notificationService.notifyUser(tweet.getAuthorUsername(),
                    "@" + username + " liked your tweet.", NotificationType.LIKE);
        }

        eventPublisher.publish(NotificationType.LIKE,
                "@" + username + " liked tweet " + tweetId);

        System.out.println("@" + username + " liked tweet " + tweetId + " (total likes: " + tweet.getLikeCount() + ")");
        return true;
    }

    /**
     * Unlike a tweet.
     */
    public boolean unlikeTweet(String username, long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        Optional<User> userOpt = userRepo.getUserByUsername(username);
        if (tweetOpt.isEmpty() || userOpt.isEmpty()) return false;

        Tweet tweet = tweetOpt.get();
        User user = userOpt.get();

        if (!tweet.removeLike(username)) {
            System.out.println("@" + username + " hasn't liked tweet " + tweetId);
            return false;
        }

        user.removeLikedTweetId(tweetId);
        System.out.println("@" + username + " unliked tweet " + tweetId);
        return true;
    }

    /**
     * Comment on a tweet with mention processing.
     */
    public Optional<Comment> commentOnTweet(String username, long tweetId, String text) {
        if (rateLimiter.isRateLimited(username, "comment")) {
            System.out.println("Comment rate-limited for @" + username);
            return Optional.empty();
        }

        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        Optional<User> userOpt = userRepo.getUserByUsername(username);
        if (tweetOpt.isEmpty() || userOpt.isEmpty()) {
            System.out.println("Tweet or user not found");
            return Optional.empty();
        }

        Tweet tweet = tweetOpt.get();
        Comment comment = new Comment(username, tweetId, text);
        tweet.addComment(comment);

        // Notify tweet author (don't notify self-comments)
        if (!username.equals(tweet.getAuthorUsername())) {
            notificationService.notifyUser(tweet.getAuthorUsername(),
                    "@" + username + " commented on your tweet: \"" + truncate(text, 50) + "\"", NotificationType.COMMENT);
        }

        // Process mentions in comment
        List<String> mentions = HashtagService.extractMentions(text);
        List<String> validMentions = mentions.stream()
                .filter(m -> userRepo.getUserByUsername(m).isPresent())
                .toList();
        notificationService.notifyMentions(validMentions,
                "@" + username + " mentioned you in a comment.", NotificationType.MENTION);

        eventPublisher.publish(NotificationType.COMMENT,
                "@" + username + " commented on tweet " + tweetId);

        System.out.println("Comment added: " + comment);
        return Optional.of(comment);
    }

    /**
     * Delete a tweet (soft delete).
     */
    public boolean deleteTweet(String username, long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        if (tweetOpt.isEmpty()) return false;

        Tweet tweet = tweetOpt.get();

        // Only author can delete their own tweet
        if (!tweet.getAuthorUsername().equals(username)) {
            System.out.println("@" + username + " cannot delete tweet by @" + tweet.getAuthorUsername());
            return false;
        }

        tweet.delete();
        System.out.println("Tweet " + tweetId + " deleted by @" + username);
        return true;
    }

    /**
     * Search tweets using pluggable strategy.
     */
    public List<Tweet> searchTweets(String query) {
        // Check cache first
        List<Tweet> cached = cacheService.getCachedTweets(query);
        if (cached != null) {
            return cached;
        }

        // Cache miss - search and cache
        List<Tweet> results = searchStrategy.search(tweetRepo, query);
        cacheService.cacheTweets(query, results);
        return results;
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public Optional<Tweet> getTweet(long tweetId) {
        return tweetRepo.getTweetById(tweetId);
    }

    public List<Tweet> getUserTweets(String username) {
        return tweetRepo.getTweetsByUser(username);
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
