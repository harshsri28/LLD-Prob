package org.example.command;

import org.example.services.TweetService;

public class CommentOnTweetCommand implements Command {
    private TweetService tweetService;
    private String userId;
    private long tweetId;
    private String text;

    public CommentOnTweetCommand(TweetService tweetService, String userId, long tweetId, String text) {
        this.tweetService = tweetService;
        this.userId = userId;
        this.tweetId = tweetId;
        this.text = text;
    }

    @Override
    public void execute() {
        tweetService.commentOnTweet(userId, tweetId, text);
    }
}
