package org.example.strategy.codeExecutor;

import org.example.models.ExecutionResult;
import org.example.models.Submission;

public interface CodeExecutor {
    ExecutionResult execute(Submission submission);
}
