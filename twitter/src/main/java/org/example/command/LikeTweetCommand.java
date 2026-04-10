package org.example.command;

import org.example.services.TweetService;

public class LikeTweetCommand implements Command {
    private TweetService tweetService;
    private String userId;
    private long tweetId;

    public LikeTweetCommand(TweetService tweetService, String userId, long tweetId) {
        this.tweetService = tweetService;
        this.userId = userId;
        this.tweetId = tweetId;
    }

    @Override
    public void execute() {
        tweetService.likeTweet(userId, tweetId);
    }
}
