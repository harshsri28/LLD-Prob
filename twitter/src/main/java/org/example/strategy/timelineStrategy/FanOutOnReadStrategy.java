package org.example.strategy.timelineStrategy;

import org.example.models.Tweet;
import org.example.models.User;
import org.example.repository.TimelineRepository;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fan-out on Read strategy.
 * When a user requests their timeline, dynamically fetch tweets from all followed users.
 *
 * Pros: No write amplification, works well for celebrities
 * Cons: Slower reads (must merge tweets from multiple users), higher read latency
 *
 * Best for: Celebrity users with > 10,000 followers
 */
public class FanOutOnReadStrategy implements TimelineStrategy {

    @Override
    public List<Tweet> getTimeline(String userId, int page, int pageSize,
                                   UserRepository userRepo, TweetRepository tweetRepo, TimelineRepository timelineRepo) {
        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) {
            return new ArrayList<>();
        }

        User user = userOpt.get();

        // Fetch recent tweets from all followed users and merge (following set stores userIds)
        List<Tweet> allTweets = new ArrayList<>();
        for (String followedUserId : user.getFollowing()) {
            allTweets.addAll(tweetRepo.getTweetsByUser(followedUserId));
        }
        // Include own tweets
        allTweets.addAll(tweetRepo.getTweetsByUser(userId));

        // Sort by creation time descending and paginate
        return allTweets.stream()
                .sorted(Comparator.comparing(Tweet::getCreatedAt).reversed())
                .skip((long) page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public void onNewTweet(Tweet tweet, User author,
                           UserRepository userRepo, TimelineRepository timelineRepo) {
        // No-op for fan-out on read: timeline is computed at read time
        System.out.println("Fan-out on read: tweet " + tweet.getId() + " stored, timeline computed at read time");
    }
}
