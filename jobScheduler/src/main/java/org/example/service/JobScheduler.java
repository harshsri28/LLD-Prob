package org.example.service;

import org.example.models.Job;
import org.example.strategy.jobListenerStrategy.JobListener;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobScheduler {
    static JobScheduler instance = new JobScheduler();

    PriorityQueue<Job> readyQueue = new PriorityQueue<>((a,b) -> b.getPriority() - a.getPriority());
    PriorityQueue<Job> waitingQueue = new PriorityQueue<>((a,b) -> Long.compare(a.getNextExecutionTime(), b.getNextExecutionTime()));

    Set<String> completedJobs = ConcurrentHashMap.newKeySet();
    Set<String> activeLocks = ConcurrentHashMap.newKeySet();

    List<JobListener> listeners = new ArrayList<>();
    DependencyResolver resolver = new DependencyResolver();

    ExecutorService workers = Executors.newFixedThreadPool(4);

    private JobScheduler() {}

    public static JobScheduler getInstance() {
        return instance;
    }

    public void registerListener(JobListener l) {
        listeners.add(l);
    }

    public void submit(Job job) {
        schedule(job);
    }

    private void schedule(Job job) {
        java.time.Instant next = job.getSchedulingStrategy().nextExecutionTime();
        if (next == null) return;

        job.setNextExecutionTime(next.toEpochMilli());

        if (job.getNextExecutionTime() <= System.currentTimeMillis()) {
            readyQueue.offer(job);
        } else {
            waitingQueue.offer(job);
        }
    }

    public void start() {
        while (true) {
            long now = System.currentTimeMillis();

            while (!waitingQueue.isEmpty() && waitingQueue.peek().getNextExecutionTime() <= now) {
                readyQueue.offer(waitingQueue.poll());
            }

            Job job = readyQueue.poll();

            if (job == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            if (!resolver.canExecute(job, completedJobs)) {
                readyQueue.offer(job);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            workers.submit(() -> execute(job));
        }
    }

    private void execute(Job job) {
        if (!activeLocks.add(job.getId())) {
            return;
        }

        try {
            listeners.forEach(l -> l.onStart(job));
            int attempt = 0;

            while (true) {
                try {
                    job.getCommand().execute();
                    completedJobs.add(job.getId());
                    listeners.forEach(l -> l.onSuccess(job));
                    break;
                } catch (Exception e) {
                    attempt++;
                    if (job.getRetryPolicy() == null || !job.getRetryPolicy().shouldRetry(attempt)) {
                        listeners.forEach(l -> l.onFailure(job, e));
                        break;
                    }
                    try {
                        Thread.sleep(job.getRetryPolicy().nextDelayMs(attempt));
                    } catch (InterruptedException ignored) {}
                }
            }

            if (job.getSchedulingStrategy().isRecurring()) {
                schedule(job);
            }
        } finally {
            activeLocks.remove(job.getId());
        }
    }
}
