package org.example.observer;

import org.example.enums.NotificationType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationService {
    private List<NotificationObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }

    public void notifyObservers(NotificationType type, String message) {
        String formatted = "[" + type.name() + "] " + message;
        notifyObservers(formatted);
    }
}
