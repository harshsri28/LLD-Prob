package org.example.services;

import org.example.models.Hashtag;
import org.example.repository.HashtagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Hashtag Service - owns hashtag data and trending computation.
 *
 * Fixes Problem 7: No Data Ownership
 * - Hashtag aggregate owns hashtag state
 * - Trending computed from HashtagRepository (not a raw HashMap)
 * - In production: streaming aggregation with windowed counts
 */
public class HashtagService {
    private HashtagRepository hashtagRepo;

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    public HashtagService(HashtagRepository hashtagRepo) {
        this.hashtagRepo = hashtagRepo;
    }

    /**
     * Extract hashtags from text (e.g., "#java #coding" -> ["java", "coding"])
     */
    public static List<String> extractHashtags(String text) {
        List<String> hashtags = new ArrayList<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        while (matcher.find()) {
            hashtags.add(matcher.group(1).toLowerCase());
        }
        return hashtags;
    }

    /**
     * Extract mentions from text (e.g., "@alice @bob" -> ["alice", "bob"])
     */
    public static List<String> extractMentions(String text) {
        List<String> mentions = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(text);
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        return mentions;
    }

    /**
     * Index hashtags from a tweet.
     */
    public void indexHashtags(List<String> hashtags, long tweetId) {
        for (String tag : hashtags) {
            Hashtag hashtag = hashtagRepo.getOrCreate(tag);
            hashtag.addTweetId(tweetId);
        }
    }

    /**
     * Get trending hashtags (top K by count).
     */
    public List<String> getTrending(int limit) {
        return hashtagRepo.getTrending(limit).stream()
                .map(h -> "#" + h.getTag() + " (" + h.getCount() + ")")
                .collect(Collectors.toList());
    }

    /**
     * Search hashtags matching a query.
     */
    public List<Hashtag> searchHashtags(String query) {
        return hashtagRepo.searchByTag(query);
    }
}
