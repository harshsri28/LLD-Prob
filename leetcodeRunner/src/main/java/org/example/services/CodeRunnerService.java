package org.example.services;

import org.example.factory.CodeExecutorFactory;
import org.example.models.ExecutionResult;
import org.example.models.Submission;
import org.example.observer.ResultObserver;
import org.example.strategy.codeExecutor.CodeExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeRunnerService {
    private final CodeExecutorFactory executorFactory;
    private final List<ResultObserver> observers;
    private final ExecutorService executorService;

    public CodeRunnerService() {
        this.executorFactory = new CodeExecutorFactory();
        this.observers = new ArrayList<>();
        // Limit to 10 concurrent executions
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void addObserver(ResultObserver observer) {
        observers.add(observer);
    }

    public CompletableFuture<ExecutionResult> runCode(Submission submission) {
        return CompletableFuture.supplyAsync(() -> {
            CodeExecutor executor = executorFactory.getExecutor(submission.getLanguage());
            return executor.execute(submission);
        }, executorService).thenApply(result -> {
            // Notify observers
            for (ResultObserver observer : observers) {
                observer.update(result);
            }
            return result;
        });
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}

