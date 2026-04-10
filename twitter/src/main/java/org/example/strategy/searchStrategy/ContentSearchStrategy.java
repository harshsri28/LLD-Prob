package org.example.strategy.searchStrategy;

import org.example.models.Tweet;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;

import java.util.List;

public class ContentSearchStrategy implements SearchStrategy {

    @Override
    public List<Tweet> search(TweetRepository repository, String query, UserRepository userRepository) {
        System.out.println("Searching tweets by content: " + query);
        return repository.searchByContent(query);
    }
}
