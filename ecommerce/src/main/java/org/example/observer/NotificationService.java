package org.example.observer;

import org.example.enums.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private List<NotificationObserver> observers = new ArrayList<>();

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
