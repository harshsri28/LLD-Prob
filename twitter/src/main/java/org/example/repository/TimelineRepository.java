package org.example.repository;

import org.example.models.Timeline;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimelineRepository {
    private Map<String, Timeline> timelines = new ConcurrentHashMap<>();

    public Timeline getOrCreateTimeline(String userId) {
        return timelines.computeIfAbsent(userId, Timeline::new);
    }

    public Timeline getTimeline(String userId) {
        return timelines.get(userId);
    }

    public void removeTimeline(String userId) {
        timelines.remove(userId);
    }
}
