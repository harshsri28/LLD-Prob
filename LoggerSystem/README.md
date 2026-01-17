# Logger System

## Problem Statement

The goal is to design and implement a flexible and extensible Logging System in Java. The system needs to handle various logging requirements, including:
- Supporting multiple log levels (DEBUG, INFO, WARN, ERROR, FATAL).
- Routing log messages to different destinations (Console, File).
- Formatting log messages in different ways (Plain Text, JSON).
- Allowing dynamic configuration of where logs should go based on their severity level.
- Ensuring the system is easy to extend with new log levels, appenders, or formatters without modifying the core logic.

## Solution

The implemented solution is a modular logging framework that leverages several behavioral and creational design patterns to achieve flexibility and separation of concerns.

The core `Logger` class acts as the entry point. When a log message is generated, it is passed to a chain of `LogHandler`s. Each handler is responsible for a specific log level. If a handler matches the message's level, it processes the message by notifying all subscribed `LogAppender`s. Each appender then formats the message using a specific `LogFormatter` and writes it to the target destination.

### Key Components
- **Logger**: The main client interface for logging.
- **Handlers**: Processors for different log levels (e.g., `InfoHandler`, `ErrorHandler`).
- **Appenders**: Output destinations (e.g., `ConsoleAppender`, `FileAppender`).
- **Formatters**: Message formatting strategies (e.g., `JsonFormatter`, `PlainTextFornatter`).

## Design Patterns Used

### 1. Singleton Pattern
- **Usage**: The `Logger` class.
- **Reason**: Ensures that there is only one global instance of the logger throughout the application lifecycle, providing a centralized point of access.

### 2. Chain of Responsibility Pattern
- **Usage**: `LogHandler` and its subclasses (`DebugHandler`, `InfoHandler`, etc.).
- **Reason**: Decouples the sender of the log request from the receiver. The log message is passed along a chain of handlers (`Debug` -> `Info` -> `Warn` -> `Error` -> `Fatal`). Each handler decides if it can handle the message based on the log level.

### 3. Observer Pattern
- **Usage**: `LogHandler` (Subject) and `LogAppender` (Observer).
- **Reason**: Allows multiple appenders to subscribe to a specific log handler. When a handler processes a message, it notifies all subscribed appenders. This makes it easy to add multiple outputs (e.g., write to both Console and File) for a single log level.

### 4. Strategy Pattern
- **Usage**: `LogFormatter` (and `LogAppender` to some extent).
- **Reason**: Defines a family of formatting algorithms (JSON, Plain Text) and makes them interchangeable. The `LogAppender` can be configured with any `LogFormatter` at runtime, allowing the format to vary independently from the output destination.

## Project Structure

```
src/main/java/org/example/
├── appenders/          # Output destinations (Console, File)
├── enums/              # Log levels
├── formatter/          # Formatting logic (JSON, PlainText)
├── handlers/           # Chain of responsibility handlers
├── models/             # Data models (LogMessage)
├── services/           # Core services (Logger, Configuration)
└── Main.java           # Entry point / Demo
```

## How to Run

1.  **Build the project**:
    ```bash
    ./gradlew build
    ```
2.  **Run the application**:
    ```bash
    ./gradlew run
    ```
    *Or run the `Main.java` class directly from your IDE.*

## Example Usage

```java
// Get Logger instance
Logger logger = Logger.getInstance();

// Configure Appenders
LogHandlerConfiguration.addAppenderForLevel(Loglevel.INFO, new ConsoleAppender(new PlainTextFornatter()));
LogHandlerConfiguration.addAppenderForLevel(Loglevel.ERROR, new FileAppender(new JsonFormatter(), "error.log"));

// Log messages
logger.info("This is an info message");
logger.error("This is an error message");
```
