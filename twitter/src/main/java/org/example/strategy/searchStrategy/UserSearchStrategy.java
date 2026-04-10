package org.example.strategy.searchStrategy;

import org.example.models.Tweet;
import org.example.models.User;
import org.example.repository.TweetRepository;
import org.example.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserSearchStrategy implements SearchStrategy {

    @Override
    public List<Tweet> search(TweetRepository repository, String query, UserRepository userRepository) {
        System.out.println("Searching tweets by user: @" + query);
        Optional<User> userOpt = userRepository.getUserByUsername(query);
        if (userOpt.isEmpty()) {
            return new ArrayList<>();
        }
        return repository.getTweetsByUser(userOpt.get().getUserId());
    }
}
