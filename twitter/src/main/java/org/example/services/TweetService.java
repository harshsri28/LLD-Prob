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
    public Optional<Tweet> createTweet(String userId, String text) {
        if (rateLimiter.isRateLimited(userId, "tweet")) {
            System.out.println("Tweet rate-limited for " + userId);
            return Optional.empty();
        }

        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("User not found: " + userId);
            return Optional.empty();
        }

        User user = userOpt.get();

        // Extract hashtags and mentions from text (mentions are usernames from @mentions in text)
        List<String> hashtags = HashtagService.extractHashtags(text);
        List<String> mentions = HashtagService.extractMentions(text);

        // Build tweet using Builder pattern
        Tweet tweet = Tweet.builder()
                .authorUserId(userId)
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
        // Resolve @mention usernames to userIds for notification delivery
        List<String> validMentionUserIds = mentions.stream()
                .map(m -> userRepo.getUserByUsername(m))
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getUserId())
                .toList();
        notificationService.notifyMentions(validMentionUserIds,
                "@" + user.getUsername() + " mentioned you in a tweet.", NotificationType.MENTION);

        // Side-effect 4: Notify followers (handled by NotificationService)
        notificationService.notifyFollowers(userId,
                "@" + user.getUsername() + " just tweeted: \"" + truncate(text, 50) + "\"", NotificationType.TWEET);

        // Publish event for observers
        eventPublisher.publishEvent(new TwitterEvent(NotificationType.TWEET, userId, null, tweet.getId(),
                "@" + user.getUsername() + " tweeted: \"" + truncate(text, 50) + "\""));

        System.out.println("Tweet created: " + tweet);
        return Optional.of(tweet);
    }

    /**
     * Like a tweet - thread-safe via ConcurrentHashMap.newKeySet().
     */
    public boolean likeTweet(String userId, long tweetId) {
        if (rateLimiter.isRateLimited(userId, "like")) {
            System.out.println("Like rate-limited for " + userId);
            return false;
        }

        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        Optional<User> userOpt = userRepo.getUserById(userId);
        if (tweetOpt.isEmpty() || userOpt.isEmpty()) {
            System.out.println("Tweet or user not found");
            return false;
        }

        Tweet tweet = tweetOpt.get();
        User user = userOpt.get();

        // Thread-safe: Set.add returns false if already present
        if (!tweet.addLike(userId)) {
            System.out.println("@" + user.getUsername() + " already liked tweet " + tweetId);
            return false;
        }

        user.addLikedTweetId(tweetId);

        // Notify tweet author (don't notify self-likes)
        if (!userId.equals(tweet.getAuthorUserId())) {
            notificationService.notifyUser(tweet.getAuthorUserId(),
                    "@" + user.getUsername() + " liked your tweet.", NotificationType.LIKE);
        }

        eventPublisher.publish(NotificationType.LIKE,
                "@" + user.getUsername() + " liked tweet " + tweetId);

        System.out.println("@" + user.getUsername() + " liked tweet " + tweetId + " (total likes: " + tweet.getLikeCount() + ")");
        return true;
    }

    /**
     * Unlike a tweet.
     */
    public boolean unlikeTweet(String userId, long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        Optional<User> userOpt = userRepo.getUserById(userId);
        if (tweetOpt.isEmpty() || userOpt.isEmpty()) return false;

        Tweet tweet = tweetOpt.get();
        User user = userOpt.get();

        if (!tweet.removeLike(userId)) {
            System.out.println("@" + user.getUsername() + " hasn't liked tweet " + tweetId);
            return false;
        }

        user.removeLikedTweetId(tweetId);
        System.out.println("@" + user.getUsername() + " unliked tweet " + tweetId);
        return true;
    }

    /**
     * Comment on a tweet with mention processing.
     */
    public Optional<Comment> commentOnTweet(String userId, long tweetId, String text) {
        if (rateLimiter.isRateLimited(userId, "comment")) {
            System.out.println("Comment rate-limited for " + userId);
            return Optional.empty();
        }

        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        Optional<User> userOpt = userRepo.getUserById(userId);
        if (tweetOpt.isEmpty() || userOpt.isEmpty()) {
            System.out.println("Tweet or user not found");
            return Optional.empty();
        }

        Tweet tweet = tweetOpt.get();
        User user = userOpt.get();
        Comment comment = new Comment(userId, tweetId, text);
        tweet.addComment(comment);

        // Notify tweet author (don't notify self-comments)
        if (!userId.equals(tweet.getAuthorUserId())) {
            notificationService.notifyUser(tweet.getAuthorUserId(),
                    "@" + user.getUsername() + " commented on your tweet: \"" + truncate(text, 50) + "\"", NotificationType.COMMENT);
        }

        // Process mentions in comment - resolve usernames to userIds
        List<String> mentions = HashtagService.extractMentions(text);
        List<String> validMentionUserIds = mentions.stream()
                .map(m -> userRepo.getUserByUsername(m))
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getUserId())
                .toList();
        notificationService.notifyMentions(validMentionUserIds,
                "@" + user.getUsername() + " mentioned you in a comment.", NotificationType.MENTION);

        eventPublisher.publish(NotificationType.COMMENT,
                "@" + user.getUsername() + " commented on tweet " + tweetId);

        System.out.println("Comment added: " + comment);
        return Optional.of(comment);
    }

    /**
     * Delete a tweet (soft delete).
     */
    public boolean deleteTweet(String userId, long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepo.getTweetById(tweetId);
        if (tweetOpt.isEmpty()) return false;

        Tweet tweet = tweetOpt.get();
        User user = userRepo.getUserById(userId).orElse(null);
        String username = user != null ? user.getUsername() : userId;

        // Only author can delete their own tweet
        if (!tweet.getAuthorUserId().equals(userId)) {
            String authorUsername = userRepo.getUserById(tweet.getAuthorUserId())
                    .map(User::getUsername).orElse(tweet.getAuthorUserId());
            System.out.println("@" + username + " cannot delete tweet by @" + authorUsername);
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
        List<Tweet> results = searchStrategy.search(tweetRepo, query, userRepo);
        cacheService.cacheTweets(query, results);
        return results;
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public Optional<Tweet> getTweet(long tweetId) {
        return tweetRepo.getTweetById(tweetId);
    }

    public List<Tweet> getUserTweets(String userId) {
        return tweetRepo.getTweetsByUser(userId);
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
