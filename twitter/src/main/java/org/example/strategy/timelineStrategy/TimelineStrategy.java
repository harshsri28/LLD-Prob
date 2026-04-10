package org.example.strategy.timelineStrategy;

import org.example.models.Tweet;
import org.example.models.User;
import org.example.repository.TimelineRepository;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;

import java.util.List;

/**
 * Strategy interface for timeline generation.
 * Twitter uses a hybrid model:
 * - Fan-out on write for normal users (fast reads)
 * - Fan-out on read for celebrities (avoid massive write amplification)
 */
public interface TimelineStrategy {
    List<Tweet> getTimeline(String userId, int page, int pageSize,
                            UserRepository userRepo, TweetRepository tweetRepo, TimelineRepository timelineRepo);

    void onNewTweet(Tweet tweet, User author,
                    UserRepository userRepo, TimelineRepository timelineRepo);
}
