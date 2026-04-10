package org.example.command;

import org.example.services.TweetService;

public class CommentOnTweetCommand implements Command {
    private TweetService tweetService;
    private String username;
    private long tweetId;
    private String text;

    public CommentOnTweetCommand(TweetService tweetService, String username, long tweetId, String text) {
        this.tweetService = tweetService;
        this.username = username;
        this.tweetId = tweetId;
        this.text = text;
    }

    @Override
    public void execute() {
        tweetService.commentOnTweet(username, tweetId, text);
    }
}
