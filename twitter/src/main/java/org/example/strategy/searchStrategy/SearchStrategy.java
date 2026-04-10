package org.example.strategy.searchStrategy;

import org.example.models.Tweet;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;

import java.util.List;

public interface SearchStrategy {
    List<Tweet> search(TweetRepository tweetRepository, String query, UserRepository userRepository);
}
