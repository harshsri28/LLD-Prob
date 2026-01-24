# Job Scheduler – Low Level Design (Java)

## 1. Problem Statement
Design and implement a scalable, in-memory **Job Scheduler** in Java that can handle:
*   **High Concurrency**: Support for millions of jobs.
*   **Diverse Scheduling**: One-time, Fixed-rate intervals, and Cron-based scheduling.
*   **Execution Guarantees**: Priority-based execution, dependency management (DAG), and automatic retries with exponential backoff.
*   **Reliability**: Idempotency (prevent double execution) and distributed locking simulation.
*   **Observability**: Real-time monitoring of job lifecycles (start, success, failure).

The solution must follow **Clean Architecture** principles and utilize appropriate **Design Patterns** to ensure extensibility and maintainability.

---

## 2. Features Implemented
*   ✅ **Scheduling Strategies**:
    *   **One-Time**: Execute once immediately or at a specific time.
    *   **Fixed-Rate**: Execute repeatedly at a fixed interval (e.g., every 10s).
    *   **Cron-Like**: Simplified interval-based scheduling.
*   ✅ **Priority Execution**: Jobs with higher priority execute first (Max-Heap).
*   ✅ **Dependency Management**: Jobs can wait for parent jobs to complete before starting (DAG).
*   ✅ **Resilience**:
    *   **Retries**: Configurable exponential backoff for failed jobs.
    *   **Idempotency**: Distributed lock simulation to prevent duplicate execution in a cluster.
    *   **Failure Handling**: Recurring jobs are rescheduled even if the current instance fails (after retries).
*   ✅ **Efficiency**:
    *   **Dual-Queue Architecture**: Separates "Waiting" jobs (Time-based) from "Ready" jobs (Priority-based) to eliminate busy waiting.
    *   **Non-Blocking**: Uses a worker thread pool for execution.

---

## 3. High-Level Architecture & Core Logic

### The Dual-Queue System
To handle both **Time** and **Priority** efficiently, the scheduler uses two priority queues:
1.  **Waiting Queue (Min-Heap by Time)**: Stores jobs scheduled for the future. The head is the job with the nearest execution time.
2.  **Ready Queue (Max-Heap by Priority)**: Stores jobs whose execution time has arrived. The head is the highest priority job.

**Flow**:
1.  The `start()` loop checks the `Waiting Queue`.
2.  If `job.nextExecutionTime <= now`, it moves the job to the `Ready Queue`.
3.  The `Ready Queue` releases the highest priority job to a Worker Thread.

### Distributed Locking (Idempotency)
To simulate a distributed environment where multiple scheduler instances might pick up the same job:
*   A `ConcurrentSet` named `activeLocks` acts as a distributed lock manager.
*   Before execution, a worker attempts to `add(jobId)` to the set.
*   If successful, it proceeds. If false (lock already held), the execution is skipped.
*   The lock is released in a `finally` block after execution/retry.

---

## 4. Design Patterns Used & Why

### 1. Singleton Pattern
*   **Where**: `JobScheduler` class.
*   **Why**: We need a single central coordinator to manage the queues and thread pool. Multiple instances would lead to resource contention and duplicate scheduling within the same JVM.

### 2. Builder Pattern
*   **Where**: `Job.Builder`
*   **Why**: A `Job` has many optional parameters (priority, strategy, retry policy, dependencies). The Builder pattern makes object creation readable and prevents "telescoping constructor" anti-patterns.
    ```java
    new Job.Builder().id("A").priority(10).strategy(new OneTimeStrategy()).build();
    ```

### 3. Strategy Pattern
*   **Where**: `SchedulingStrategy`, `RetryPolicy`
*   **Why**:
    *   **Scheduling**: Allows swapping algorithms (Cron vs. FixedRate) without changing the `Job` class.
    *   **Retry**: Allows different backoff logic (Exponential vs. Linear) to be plugged in dynamically.
    *   **OCP (Open/Closed Principle)**: New strategies can be added without modifying existing code.

### 4. Command Pattern
*   **Where**: `JobCommand` interface.
*   **Why**: Decouples the *scheduler* (invoker) from the *task logic* (receiver). The scheduler simply calls `execute()` without knowing what the job actually does.

### 5. Observer Pattern
*   **Where**: `JobListener`, `MetricsListener`
*   **Why**: Decouples monitoring from execution. We can add logging, alerting, or metrics collection without modifying the core execution loop.

### 6. Decorator / Wrapper Concept
*   **Where**: `JobScheduler.execute()`
*   **Why**: The execution logic wraps the raw `JobCommand` with layers of "Decorations":
    *   **Locking Layer**: Acquires/Releases lock.
    *   **Retry Layer**: Retries on exception.
    *   **Observability Layer**: Emits events (Start/Success/Fail).
    *   **Rescheduling Layer**: Re-queues recurring jobs.

---

## 5. Project Structure

```
src/main/java/org/example/
├── Main.java                        # Entry point / Demo
├── models/
│   └── Job.java                     # Core Entity
├── service/
│   ├── JobScheduler.java            # Singleton Controller (Queues + Locks)
│   └── DependencyResolver.java      # DAG Logic
└── strategy/
    ├── jobCommand/                  # Command Pattern
    │   └── JobCommand.java
    ├── jobListenerStrategy/         # Observer Pattern
    │   ├── JobListener.java
    │   └── MetricsListener.java
    ├── retryPolicyStrategy/         # Strategy Pattern
    │   ├── RetryPolicy.java
    │   └── ExponentialBackoffRetry.java
    └── schedulingStrategy/          # Strategy Pattern
        ├── SchedulingStrategy.java
        ├── OneTimeStrategy.java
        ├── FixedRateStrategy.java
        └── CronStrategy.java
```

---

## 6. How to Run

### Prerequisites
*   Java 8 or higher.

### Compilation & Execution
1.  **Compile**:
    ```bash
    javac -source 8 -target 8 -d out src/main/java/org/example/**/*.java
    ```

2.  **Run**:
    ```bash
    java -cp out org.example.Main
    ```

### Sample Output
```
[METRIC] Job started: A
Job A executed
[METRIC] Job success: A
[METRIC] Job started: B
Job B executed
[METRIC] Job success: B
[METRIC] Job started: C
Cron Job C executed
[METRIC] Job success: C
```

---

## 7. Future Improvements (Production Scale)
To scale this beyond a single JVM:
1.  **Storage**: Replace in-memory `PriorityQueue` with **Redis Sorted Sets** or **Kafka**.
2.  **Locking**: Replace `ConcurrentHashMap` with **Redis Distributed Locks (Redlock)** or **Zookeeper**.
3.  **Persistence**: Store job status in a database (PostgreSQL/MySQL) to survive restarts.
