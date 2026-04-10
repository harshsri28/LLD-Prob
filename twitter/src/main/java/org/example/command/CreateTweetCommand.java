package org.example.command;

import org.example.services.TweetService;

public class CreateTweetCommand implements Command {
    private TweetService tweetService;
    private String username;
    private String text;

    public CreateTweetCommand(TweetService tweetService, String username, String text) {
        this.tweetService = tweetService;
        this.username = username;
        this.text = text;
    }

    @Override
    public void execute() {
        tweetService.createTweet(username, text);
    }
}
