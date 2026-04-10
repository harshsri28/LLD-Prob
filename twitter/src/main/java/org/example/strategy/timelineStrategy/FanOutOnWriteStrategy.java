package org.example.strategy.timelineStrategy;

import org.example.models.Timeline;
import org.example.models.Tweet;
import org.example.models.User;
import org.example.repository.TimelineRepository;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;

import java.time.ZoneOffset;
import java.util.List;

/**
 * Fan-out on Write strategy.
 * When a user tweets, push the tweet ID to all followers' timelines.
 *
 * Pros: Fast reads (O(1) per page), timelines pre-computed
 * Cons: Slow writes for users with many followers, storage overhead
 *
 * Best for: Normal users with < 10,000 followers
 */
public class FanOutOnWriteStrategy implements TimelineStrategy {

    @Override
    public List<Tweet> getTimeline(String userId, int page, int pageSize,
                                   UserRepository userRepo, TweetRepository tweetRepo, TimelineRepository timelineRepo) {
        Timeline timeline = timelineRepo.getOrCreateTimeline(userId);
        List<Long> tweetIds = timeline.getPage(page, pageSize);
        return tweetRepo.getTweetsByIds(tweetIds);
    }

    @Override
    public void onNewTweet(Tweet tweet, User author,
                           UserRepository userRepo, TimelineRepository timelineRepo) {
        long createdAtMillis = tweet.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        // Push to all followers' timelines (followers set now stores userIds)
        for (String followerUserId : author.getFollowers()) {
            Timeline followerTimeline = timelineRepo.getOrCreateTimeline(followerUserId);
            followerTimeline.addTweet(tweet.getId(), createdAtMillis);
        }

        // Also add to author's own timeline
        Timeline authorTimeline = timelineRepo.getOrCreateTimeline(author.getUserId());
        authorTimeline.addTweet(tweet.getId(), createdAtMillis);

        System.out.println("Fan-out on write: pushed tweet " + tweet.getId() + " to " + author.getFollowers().size() + " followers' timelines");
    }
}
