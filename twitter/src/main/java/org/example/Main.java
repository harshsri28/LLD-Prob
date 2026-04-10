package org.example;

import org.example.cache.CacheService;
import org.example.command.Command;
import org.example.command.CommentOnTweetCommand;
import org.example.command.CreateTweetCommand;
import org.example.command.FollowUserCommand;
import org.example.command.LikeTweetCommand;
import org.example.factory.SearchFactory;
import org.example.models.Tweet;
import org.example.models.User;
import org.example.observer.EventPublisher;
import org.example.repository.HashtagRepository;
import org.example.repository.TimelineRepository;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;
import org.example.services.HashtagService;
import org.example.services.NotificationService;
import org.example.services.RateLimiterService;
import org.example.services.TimelineService;
import org.example.services.TweetService;
import org.example.services.UserService;
import org.example.strategy.searchStrategy.SearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // ============================================================
        // 1. Initialize Repositories (Data Access Layer)
        // All repositories use ConcurrentHashMap for thread-safety
        // ============================================================
        System.out.println("=== 1. Initializing Repositories ===");
        UserRepository userRepo = new UserRepository();
        TweetRepository tweetRepo = new TweetRepository();
        TimelineRepository timelineRepo = new TimelineRepository();
        HashtagRepository hashtagRepo = new HashtagRepository();
        System.out.println("Repositories initialized with ConcurrentHashMap storage.\n");

        // ============================================================
        // 2. Initialize Event Publisher (Observer Pattern)
        // CopyOnWriteArrayList for thread-safe observer list
        // ============================================================
        System.out.println("=== 2. Initializing Event System ===");
        EventPublisher eventPublisher = new EventPublisher();
        System.out.println("Event publisher initialized (CopyOnWriteArrayList).\n");

        // ============================================================
        // 3. Initialize Services (Dependency Injection)
        // Each service owns its domain - clear aggregate boundaries
        // ============================================================
        System.out.println("=== 3. Initializing Services ===");
        RateLimiterService rateLimiter = new RateLimiterService();
        // Disable rate limiting initially for demo setup
        rateLimiter.setActionCooldown("tweet", 0);
        rateLimiter.setActionCooldown("follow", 0);
        rateLimiter.setActionCooldown("like", 0);
        rateLimiter.setActionCooldown("comment", 0);

        NotificationService notificationService = new NotificationService(userRepo);
        HashtagService hashtagService = new HashtagService(hashtagRepo);
        TimelineService timelineService = new TimelineService(userRepo, tweetRepo, timelineRepo);
        CacheService cacheService = new CacheService(30000); // 30s TTL

        SearchStrategy contentSearch = SearchFactory.getSearchStrategy("CONTENT");
        TweetService tweetService = TweetService.getInstance(tweetRepo, userRepo, rateLimiter,
                hashtagService, notificationService, timelineService, cacheService, eventPublisher, contentSearch);
        UserService userService = new UserService(userRepo, rateLimiter, notificationService, eventPublisher);
        System.out.println("All services initialized with proper ownership boundaries.\n");

        // ============================================================
        // 4. Create Users (Factory Pattern)
        // ============================================================
        System.out.println("=== 4. Creating Users (Factory Pattern) ===");
        User alice = userService.createUser("alice", "Alice Johnson", "Software engineer & coffee lover", "alice.jpg");
        User bob = userService.createUser("bob", "Bob Smith", "Tech enthusiast", "bob.jpg");
        User charlie = userService.createUser("charlie", "Charlie Brown", "Writer and dreamer", "charlie.jpg");
        User diana = userService.createUser("diana", "Diana Prince", "Photographer", "diana.jpg");
        User eve = userService.createUser("eve", "Eve Taylor", "Gamer and streamer", "eve.jpg");
        System.out.println();

        // ============================================================
        // 5. Follow Users (Command Pattern + Thread-safe Sets)
        // Now using userId instead of username
        // ============================================================
        System.out.println("=== 5. Follow Users (Command Pattern) ===");
        Command followCmd1 = new FollowUserCommand(userService, bob.getUserId(), alice.getUserId());
        Command followCmd2 = new FollowUserCommand(userService, charlie.getUserId(), alice.getUserId());
        Command followCmd3 = new FollowUserCommand(userService, diana.getUserId(), alice.getUserId());
        Command followCmd4 = new FollowUserCommand(userService, eve.getUserId(), alice.getUserId());
        Command followCmd5 = new FollowUserCommand(userService, alice.getUserId(), bob.getUserId());
        Command followCmd6 = new FollowUserCommand(userService, charlie.getUserId(), bob.getUserId());
        Command followCmd7 = new FollowUserCommand(userService, diana.getUserId(), charlie.getUserId());

        followCmd1.execute();
        followCmd2.execute();
        followCmd3.execute();
        followCmd4.execute();
        followCmd5.execute();
        followCmd6.execute();
        followCmd7.execute();

        System.out.println("\nFollower counts:");
        System.out.println("  @alice: " + alice.getFollowerCount() + " followers, " + alice.getFollowingCount() + " following");
        System.out.println("  @bob: " + bob.getFollowerCount() + " followers, " + bob.getFollowingCount() + " following");
        System.out.println("  @charlie: " + charlie.getFollowerCount() + " followers, " + charlie.getFollowingCount() + " following");
        System.out.println();

        // ============================================================
        // 6. Create Tweets (Builder Pattern + Event-Driven Pipeline)
        // Single responsibility: persist + publish event
        // Side-effects handled by consumers (timeline, hashtags, mentions, notifications)
        // ============================================================
        System.out.println("=== 6. Create Tweets (Builder + Event Pipeline) ===");

        Command tweetCmd1 = new CreateTweetCommand(tweetService, alice.getUserId(),
                "Hello Twitter! Excited to start coding in #java #programming @bob");
        tweetCmd1.execute();
        System.out.println();

        Command tweetCmd2 = new CreateTweetCommand(tweetService, alice.getUserId(),
                "Just deployed my #microservices app! #java #devops");
        tweetCmd2.execute();
        System.out.println();

        Command tweetCmd3 = new CreateTweetCommand(tweetService, bob.getUserId(),
                "Working on a new #opensource project #coding #java");
        tweetCmd3.execute();
        System.out.println();

        Optional<Tweet> charliesTweet = tweetService.createTweet(charlie.getUserId(),
                "Beautiful sunset today! #photography @diana");
        System.out.println();

        tweetService.createTweet(diana.getUserId(), "Check out my latest photo series #photography #art");
        System.out.println();

        tweetService.createTweet(eve.getUserId(), "Streaming tonight at 8pm! #gaming #twitch @alice @bob");
        System.out.println();

        // ============================================================
        // 7. Like Tweets (Thread-safe ConcurrentHashMap.newKeySet)
        // ============================================================
        System.out.println("=== 7. Like Tweets (Thread-safe Likes) ===");
        // Get tweet IDs
        List<Tweet> aliceTweets = tweetService.getUserTweets(alice.getUserId());
        if (!aliceTweets.isEmpty()) {
            long firstTweetId = aliceTweets.get(0).getId();

            Command likeCmd1 = new LikeTweetCommand(tweetService, bob.getUserId(), firstTweetId);
            likeCmd1.execute();

            Command likeCmd2 = new LikeTweetCommand(tweetService, charlie.getUserId(), firstTweetId);
            likeCmd2.execute();

            Command likeCmd3 = new LikeTweetCommand(tweetService, diana.getUserId(), firstTweetId);
            likeCmd3.execute();

            // Try duplicate like (should be rejected)
            tweetService.likeTweet(bob.getUserId(), firstTweetId);
        }
        System.out.println();

        // ============================================================
        // 8. Comment on Tweets (with @mention notifications)
        // ============================================================
        System.out.println("=== 8. Comment on Tweets (Mention Processing) ===");
        if (!aliceTweets.isEmpty()) {
            long firstTweetId = aliceTweets.get(0).getId();

            Command commentCmd1 = new CommentOnTweetCommand(tweetService, bob.getUserId(), firstTweetId,
                    "Great tweet @alice! Love the #java content");
            commentCmd1.execute();

            tweetService.commentOnTweet(charlie.getUserId(), firstTweetId,
                    "Welcome to Twitter! @alice @bob");

            tweetService.commentOnTweet(diana.getUserId(), firstTweetId,
                    "Awesome! @charlie should check this out too");
        }
        System.out.println();

        // ============================================================
        // 9. Search Tweets (Strategy Pattern + Cache)
        // ============================================================
        System.out.println("=== 9. Search Tweets (Strategy + Cache) ===");

        // Search by content (default strategy)
        System.out.println("\n--- Content Search: 'java' ---");
        List<Tweet> javaResults = tweetService.searchTweets("java");
        javaResults.forEach(t -> System.out.println("  " + t));

        // Search again - should hit cache
        System.out.println("\n--- Content Search: 'java' (should be cached) ---");
        List<Tweet> cachedResults = tweetService.searchTweets("java");
        cachedResults.forEach(t -> System.out.println("  " + t));

        // Switch to hashtag search strategy (Strategy Pattern)
        System.out.println("\n--- Switch to Hashtag Search Strategy ---");
        SearchStrategy hashtagSearch = SearchFactory.getSearchStrategy("HASHTAG");
        tweetService.setSearchStrategy(hashtagSearch);
        List<Tweet> hashtagResults = tweetService.searchTweets("photography");
        hashtagResults.forEach(t -> System.out.println("  " + t));

        // Switch to user search strategy
        System.out.println("\n--- Switch to User Search Strategy ---");
        SearchStrategy userSearch = SearchFactory.getSearchStrategy("USER");
        tweetService.setSearchStrategy(userSearch);
        List<Tweet> userResults = tweetService.searchTweets("alice");
        userResults.forEach(t -> System.out.println("  " + t));

        // Reset to content search
        tweetService.setSearchStrategy(contentSearch);
        System.out.println();

        // ============================================================
        // 10. Trending Hashtags (Stream Aggregation)
        // ============================================================
        System.out.println("=== 10. Trending Hashtags (Stream Aggregation) ===");
        List<String> trending = hashtagService.getTrending(5);
        System.out.println("Top 5 Trending:");
        trending.forEach(h -> System.out.println("  " + h));
        System.out.println();

        // ============================================================
        // 11. Home Timeline (Fan-Out on Write + Pagination)
        // ============================================================
        System.out.println("=== 11. Home Timeline (Fan-Out on Write) ===");

        System.out.println("\n--- Bob's Home Timeline (page 0, size 5) ---");
        List<Tweet> bobTimeline = timelineService.getHomeTimeline(bob.getUserId(), 0, 5);
        if (bobTimeline.isEmpty()) {
            System.out.println("  (empty - using fan-out on read fallback)");
            bobTimeline = timelineService.getHomeTimeline(bob.getUserId(), 0, 5);
        }
        bobTimeline.forEach(t -> System.out.println("  " + t));

        System.out.println("\n--- Charlie's Home Timeline (page 0, size 5) ---");
        List<Tweet> charlieTimeline = timelineService.getHomeTimeline(charlie.getUserId(), 0, 5);
        charlieTimeline.forEach(t -> System.out.println("  " + t));

        System.out.println("\n--- Alice's User Timeline ---");
        List<Tweet> aliceTimeline = timelineService.getUserTimeline(alice.getUserId());
        aliceTimeline.forEach(t -> System.out.println("  " + t));
        System.out.println();

        // ============================================================
        // 12. Unfollow User
        // ============================================================
        System.out.println("=== 12. Unfollow User ===");
        userService.unfollowUser(eve.getUserId(), alice.getUserId());
        System.out.println("@alice now has " + alice.getFollowerCount() + " followers");
        System.out.println();

        // ============================================================
        // 13. Delete Tweet (Soft Delete)
        // ============================================================
        System.out.println("=== 13. Delete Tweet ===");
        if (!aliceTweets.isEmpty()) {
            long secondTweetId = aliceTweets.size() > 1 ? aliceTweets.get(1).getId() : aliceTweets.get(0).getId();
            tweetService.deleteTweet(alice.getUserId(), secondTweetId);

            // Verify deletion - search shouldn't return deleted tweets
            System.out.println("Alice's active tweets after deletion:");
            tweetService.getUserTweets(alice.getUserId()).forEach(t -> System.out.println("  " + t));
        }
        System.out.println();

        // ============================================================
        // 14. User Notifications Summary
        // ============================================================
        System.out.println("=== 14. User Notifications Summary ===");
        for (User user : userService.getAllUsers()) {
            System.out.println("@" + user.getUsername() + " has " + user.getNotifications().size() + " notifications");
            user.getNotifications().forEach(n -> System.out.println("    " + n));
        }
        System.out.println();

        // ============================================================
        // 15. Rate Limiting Demo
        // ============================================================
        System.out.println("=== 15. Rate Limiting Demo ===");
        // Create a fresh user who hasn't tweeted yet to demonstrate rate limiting
        User rateLimitTestUser = userService.createUser("ratelimituser", "Rate Limit Tester");
        rateLimiter.setActionCooldown("tweet", 5000); // Set 5 second cooldown

        Optional<Tweet> rateLimitedTweet = tweetService.createTweet(rateLimitTestUser.getUserId(), "First tweet - this should work!");
        System.out.println("First tweet: " + (rateLimitedTweet.isPresent() ? "SUCCESS" : "RATE LIMITED"));

        // Immediately try again - should be rate limited (within 5 second window)
        Optional<Tweet> blockedTweet = tweetService.createTweet(rateLimitTestUser.getUserId(), "Second tweet - this should be blocked!");
        System.out.println("Immediate retry: " + (blockedTweet.isPresent() ? "SUCCESS" : "RATE LIMITED"));
        rateLimiter.setActionCooldown("tweet", 0); // Reset for concurrent demo
        System.out.println();

        // ============================================================
        // 16. Concurrent Like Simulation
        // "What happens if 1,000 users like the same tweet simultaneously?"
        // Answer: Thread-safe via ConcurrentHashMap.newKeySet()
        // ============================================================
        System.out.println("=== 16. Concurrent Like Simulation ===");

        // Create a tweet to be liked concurrently
        Optional<Tweet> popularTweet = tweetService.createTweet(alice.getUserId(), "This will be liked by many users concurrently!");
        if (popularTweet.isPresent()) {
            long popularTweetId = popularTweet.get().getId();

            // Create 20 temporary users to simulate concurrent likes
            List<User> tempUsers = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                tempUsers.add(userService.createUser("tempuser" + i, "Temp User " + i));
            }

            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < 20; i++) {
                final String tempUserId = tempUsers.get(i).getUserId();
                executor.submit(() -> {
                    tweetService.likeTweet(tempUserId, popularTweetId);
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            Tweet updatedTweet = tweetService.getTweet(popularTweetId).orElse(null);
            if (updatedTweet != null) {
                System.out.println("\nConcurrent likes result:");
                System.out.println("  Expected likes: 20");
                System.out.println("  Actual likes: " + updatedTweet.getLikeCount());
                System.out.println("  Thread-safe: " + (updatedTweet.getLikeCount() == 20 ? "YES" : "NO - DATA RACE!"));
            }
        }
        System.out.println();

        // ============================================================
        // 17. Concurrent Tweet Simulation
        // Multiple users tweeting simultaneously
        // ============================================================
        System.out.println("=== 17. Concurrent Tweet Simulation ===");

        ExecutorService tweetExecutor = Executors.newFixedThreadPool(3);

        final String aliceId = alice.getUserId();
        final String bobId = bob.getUserId();
        final String charlieId = charlie.getUserId();

        Runnable aliceTweeting = () -> {
            for (int i = 0; i < 5; i++) {
                tweetService.createTweet(aliceId, "Alice concurrent tweet #" + i + " #concurrent");
            }
        };
        Runnable bobTweeting = () -> {
            for (int i = 0; i < 5; i++) {
                tweetService.createTweet(bobId, "Bob concurrent tweet #" + i + " #concurrent");
            }
        };
        Runnable charlieTweeting = () -> {
            for (int i = 0; i < 5; i++) {
                tweetService.createTweet(charlieId, "Charlie concurrent tweet #" + i + " #concurrent");
            }
        };

        tweetExecutor.submit(aliceTweeting);
        tweetExecutor.submit(bobTweeting);
        tweetExecutor.submit(charlieTweeting);

        tweetExecutor.shutdown();
        if (!tweetExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            tweetExecutor.shutdownNow();
        }

        System.out.println("\nConcurrent tweet results:");
        System.out.println("  Alice's total tweets: " + tweetService.getUserTweets(alice.getUserId()).size());
        System.out.println("  Bob's total tweets: " + tweetService.getUserTweets(bob.getUserId()).size());
        System.out.println("  Charlie's total tweets: " + tweetService.getUserTweets(charlie.getUserId()).size());
        System.out.println();

        // ============================================================
        // 18. Final Summary
        // ============================================================
        System.out.println("=== 18. Final Summary ===");
        System.out.println("Users: " + userService.getAllUsers().size());
        System.out.println("Trending hashtags:");
        hashtagService.getTrending(10).forEach(h -> System.out.println("  " + h));

        System.out.println("\nUser profiles:");
        for (User user : List.of(alice, bob, charlie, diana, eve)) {
            System.out.println("  " + user);
        }

        System.out.println("\n=== Design Patterns Used ===");
        System.out.println("  1. Singleton (TweetService - double-checked locking)");
        System.out.println("  2. Factory (UserFactory, SearchFactory)");
        System.out.println("  3. Strategy (SearchStrategy: Content/Hashtag/User, TimelineStrategy: FanOutOnWrite/FanOutOnRead)");
        System.out.println("  4. Observer (EventPublisher + EventObserver with CopyOnWriteArrayList)");
        System.out.println("  5. Command (CreateTweet, LikeTweet, FollowUser, CommentOnTweet)");
        System.out.println("  6. Builder (Tweet.Builder)");
        System.out.println("  7. Repository (ConcurrentHashMap-based data access)");

        System.out.println("\n=== Concurrency Mechanisms ===");
        System.out.println("  1. ConcurrentHashMap in all repositories");
        System.out.println("  2. ConcurrentHashMap.newKeySet() for followers/likes");
        System.out.println("  3. Collections.synchronizedList for tweets/notifications");
        System.out.println("  4. ConcurrentSkipListSet for ordered timelines");
        System.out.println("  5. AtomicLong/AtomicInteger for IDs and counters");
        System.out.println("  6. CopyOnWriteArrayList for observer list");
        System.out.println("  7. CAS (Compare-And-Swap) in RateLimiterService");

        System.out.println("\n=== Problems Solved ===");
        System.out.println("  Problem 1: Concurrency -> ConcurrentHashMap + Atomic ops + Service ownership");
        System.out.println("  Problem 2: God Method -> Event-driven pipeline (createTweet only persists + publishes)");
        System.out.println("  Problem 3: No Timeline -> Fan-out on write + hybrid model + pagination");
        System.out.println("  Problem 4: Sync Notifications -> NotificationService decoupled from core flows");
        System.out.println("  Problem 5: Unsafe Rate Limiter -> CAS-based token bucket with AtomicLong");
        System.out.println("  Problem 6: Bad Cache -> CacheService with TTL, eviction, ConcurrentHashMap");
        System.out.println("  Problem 7: No Ownership -> Domain aggregates (User/Tweet/Timeline/Hashtag/Notification)");

        // Cleanup
        cacheService.shutdown();
        System.out.println("\nTwitter LLD demo completed successfully!");
    }
}
