package org.example.repository;

import org.example.models.Hashtag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HashtagRepository {
    private Map<String, Hashtag> hashtags = new ConcurrentHashMap<>();

    public Hashtag getOrCreate(String tag) {
        String normalizedTag = tag.toLowerCase();
        return hashtags.computeIfAbsent(normalizedTag, Hashtag::new);
    }

    public List<Hashtag> getTrending(int limit) {
        return hashtags.values().stream()
                .sorted(Comparator.comparingInt(Hashtag::getCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Hashtag> searchByTag(String query) {
        return hashtags.values().stream()
                .filter(h -> h.getTag().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Hashtag> getAllHashtags() {
        return new ArrayList<>(hashtags.values());
    }
}
