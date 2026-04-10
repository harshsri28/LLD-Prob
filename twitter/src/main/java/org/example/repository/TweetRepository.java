package org.example.repository;

import org.example.enums.TweetStatus;
import org.example.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TweetRepository {
    private Map<Long, Tweet> tweets = new ConcurrentHashMap<>();

    public void addTweet(Tweet tweet) {
        tweets.put(tweet.getId(), tweet);
    }

    public Optional<Tweet> getTweetById(long tweetId) {
        return Optional.ofNullable(tweets.get(tweetId));
    }

    public void removeTweet(long tweetId) {
        tweets.remove(tweetId);
    }

    public List<Tweet> getAllTweets() {
        return new ArrayList<>(tweets.values());
    }

    public List<Tweet> getTweetsByUser(String userId) {
        return tweets.values().stream()
                .filter(t -> t.getAuthorUserId().equals(userId) && t.getStatus() == TweetStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Tweet> searchByContent(String query) {
        return tweets.values().stream()
                .filter(t -> t.getText().toLowerCase().contains(query.toLowerCase()) && t.getStatus() == TweetStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Tweet> searchByHashtag(String hashtag) {
        String normalizedTag = hashtag.toLowerCase().startsWith("#") ? hashtag.substring(1).toLowerCase() : hashtag.toLowerCase();
        return tweets.values().stream()
                .filter(t -> t.getHashtags().contains(normalizedTag) && t.getStatus() == TweetStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Tweet> getTweetsByIds(List<Long> tweetIds) {
        return tweetIds.stream()
                .map(tweets::get)
                .filter(t -> t != null && t.getStatus() == TweetStatus.ACTIVE)
                .collect(Collectors.toList());
    }
}
