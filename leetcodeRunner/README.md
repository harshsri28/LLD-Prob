# Code Execution Service (LeetCode Runner)

This project implements a robust, scalable service for executing user-submitted code against test cases, similar to online judge platforms like LeetCode or HackerRank. It supports multiple programming languages (currently Java and Python) and is designed using industry-standard design patterns to ensure extensibility and maintainability.

## Problem Statement

Design a service that can:
1.  Accept user-submitted code in various programming languages.
2.  Execute the code against a set of defined test cases.
3.  Validate the output against expected results.
4.  Handle execution timeouts and runtime errors.
5.  Return execution results (Success/Failure, Output, Execution Time).
6.  Handle concurrent submissions efficiently.

## Design Patterns Used

We utilized several Gang of Four (GoF) design patterns to solve specific architectural challenges:

### 1. Strategy Pattern
*   **Usage**: `CodeExecutor` interface with concrete implementations `JavaExecutor` and `PythonExecutor`.
*   **Reason**: Allows the algorithm for code execution to vary by language (compiled vs. interpreted) while keeping the client interface consistent. We can easily swap or add new language strategies without changing the core runner logic.

### 2. Factory Pattern
*   **Usage**: `CodeExecutorFactory` class.
*   **Reason**: Centralizes the logic for creating the appropriate `CodeExecutor` based on the submission language. This decouples the client (`CodeRunnerService`) from the concrete executor classes.

### 3. Template Method Pattern
*   **Usage**: `AbstractCodeExecutor` class.
*   **Reason**: Defines the skeleton of the execution algorithm (Create File → Compile (Optional) → Run → Verify → Cleanup). Concrete subclasses only implement the specific steps (e.g., `compileCode` for Java, `getRunCommand` for Python), avoiding code duplication.

### 4. Builder Pattern
*   **Usage**: `Submission.Builder` and `TestCaseBuilder`.
*   **Reason**: Simplifies the construction of complex objects like `Submission` which has multiple optional parameters (timeout, language, list of test cases). It makes the client code more readable and flexible.

### 5. Observer Pattern
*   **Usage**: `ResultObserver` interface and `LoggerObserver`.
*   **Reason**: Decouples the execution logic from the result handling. Multiple observers (loggers, database savers, notification services) can listen for execution results without modifying the runner service.

## Solution & Implementation Details

### Core Features
*   **Multi-Language Support**: Capable of running Java (compiled) and Python (interpreted) code.
*   **Sandboxed Execution**: Uses `ProcessBuilder` to run user code in separate processes, ensuring argument safety and isolation.
*   **Concurrency**: Uses `ExecutorService` (Thread Pool) and `CompletableFuture` to handle multiple submissions simultaneously without blocking the main thread or exhausting system resources.
*   **Robustness**:
    *   **Deadlock Prevention**: Reads standard output and error streams in separate threads to prevent buffer filling deadlocks.
    *   **Resource Cleanup**: Automatically deletes temporary source files and directories after execution using a `finally` block in the template method.
    *   **Timeout Handling**: Enforces execution time limits to prevent infinite loops from hanging the system.

### Project Structure
```
src/main/java/org/example/
├── factory/            # CodeExecutorFactory
├── models/             # Submission, TestCase, ExecutionResult
├── observer/           # ResultObserver, LoggerObserver
├── services/           # CodeRunnerService (Facade)
├── strategy/
│   └── codeExecutor/   # CodeExecutor, AbstractCodeExecutor, JavaExecutor, PythonExecutor
└── Main.java           # Entry point and demo
```

### How to Run

**Prerequisites**: Java 8+ and Python 3 installed.

1.  **Compile**:
    ```bash
    javac --release 8 -d out src/main/java/org/example/models/*.java src/main/java/org/example/observer/*.java src/main/java/org/example/strategy/codeExecutor/*.java src/main/java/org/example/factory/*.java src/main/java/org/example/services/*.java src/main/java/org/example/Main.java
    ```

2.  **Run**:
    ```bash
    java -cp out org.example.Main
    ```
