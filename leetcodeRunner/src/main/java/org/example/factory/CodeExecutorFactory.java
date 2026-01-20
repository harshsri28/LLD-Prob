package org.example.factory;

import org.example.strategy.codeExecutor.CodeExecutor;
import org.example.strategy.codeExecutor.JavaExecutor;
import org.example.strategy.codeExecutor.PythonExecutor;

public class CodeExecutorFactory {
    public CodeExecutor getExecutor(String language) {
        switch (language.toLowerCase()) {
            case "java":
                return new JavaExecutor();
            case "python":
                return new PythonExecutor();
            // Can be extended for other languages
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
}

