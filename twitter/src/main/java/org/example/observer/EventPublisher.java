package org.example.observer;

import org.example.enums.NotificationType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event publisher using Observer pattern with CopyOnWriteArrayList for thread-safety.
 * Decouples event producers (TweetService, UserService) from consumers
 * (TimelineService, NotificationService, HashtagService).
 *
 * In production: this would be backed by a message queue (Kafka, SQS)
 * for durability, retry, and cross-service communication.
 */
public class EventPublisher {
    private List<EventObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    public void publish(String message) {
        for (EventObserver observer : observers) {
            observer.onEvent(message);
        }
    }

    public void publish(NotificationType type, String message) {
        String formatted = "[" + type.name() + "] " + message;
        publish(formatted);
    }

    public void publishEvent(TwitterEvent event) {
        String formatted = "[" + event.getType().name() + "] " + event.getMessage();
        publish(formatted);
    }
}
