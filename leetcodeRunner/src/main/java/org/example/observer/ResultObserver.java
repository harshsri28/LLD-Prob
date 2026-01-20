package org.example.observer;

import org.example.models.ExecutionResult;

public interface ResultObserver {
    void update(ExecutionResult result);
}
