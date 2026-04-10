package org.example.models;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Hashtag {
    private String tag;
    private AtomicInteger count;
    private List<Long> tweetIds;

    public Hashtag(String tag) {
        this.tag = tag.toLowerCase();
        this.count = new AtomicInteger(0);
        this.tweetIds = Collections.synchronizedList(new ArrayList<>());
    }

    public void incrementCount() {
        count.incrementAndGet();
    }

    public void addTweetId(long tweetId) {
        tweetIds.add(tweetId);
        incrementCount();
    }

    // Getters
    public String getTag() { return tag; }
    public int getCount() { return count.get(); }
    public List<Long> getTweetIds() { return new ArrayList<>(tweetIds); }

    @Override
    public String toString() {
        return "#" + tag + " (" + count.get() + " tweets)";
    }
}
