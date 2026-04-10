package org.example.command;

import org.example.services.TweetService;

public class CreateTweetCommand implements Command {
    private TweetService tweetService;
    private String userId;
    private String text;

    public CreateTweetCommand(TweetService tweetService, String userId, String text) {
        this.tweetService = tweetService;
        this.userId = userId;
        this.text = text;
    }

    @Override
    public void execute() {
        tweetService.createTweet(userId, text);
    }
}
