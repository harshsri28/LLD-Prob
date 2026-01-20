package org.example.observer;

import org.example.models.ExecutionResult;

public class LoggerObserver implements ResultObserver {
    @Override
    public void update(ExecutionResult result) {
        System.out.println("[LOG] Execution completed: " +
                (result.isSuccess() ? "SUCCESS" : "FAILURE") +
                ", Time: " + result.getExecutionTime() + "ms");
    }
}
