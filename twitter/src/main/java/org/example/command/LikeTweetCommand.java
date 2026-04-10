package org.example.command;

import org.example.services.TweetService;

public class LikeTweetCommand implements Command {
    private TweetService tweetService;
    private String username;
    private long tweetId;

    public LikeTweetCommand(TweetService tweetService, String username, long tweetId) {
        this.tweetService = tweetService;
        this.username = username;
        this.tweetId = tweetId;
    }

    @Override
    public void execute() {
        tweetService.likeTweet(username, tweetId);
    }
}
