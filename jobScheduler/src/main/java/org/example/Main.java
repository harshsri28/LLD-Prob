package org.example;

import org.example.models.Job;
import org.example.service.JobScheduler;
import org.example.strategy.jobListenerStrategy.MetricsListener;
import org.example.strategy.retryPolicyStrategy.ExponentialBackoffRetry;
import org.example.strategy.schedulingStrategy.CronStrategy;
import org.example.strategy.schedulingStrategy.FixedRateStrategy;
import org.example.strategy.schedulingStrategy.OneTimeStrategy;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JobScheduler scheduler = JobScheduler.getInstance();
        scheduler.registerListener(new MetricsListener());

        Job jobA = new Job.Builder()
                .id("A")
                .priority(10)
                .strategy(new OneTimeStrategy())
                .command(() -> System.out.println("Job A executed"))
                .build();

        Job jobB = new Job.Builder()
                .id("B")
                .priority(5)
                .strategy(new FixedRateStrategy(Duration.ofSeconds(3)))
                .dependsOn(Arrays.asList("A"))
                .retry(new ExponentialBackoffRetry(3, 100))
                .command(() -> {
                    System.out.println("Job B executed");
                    if (Math.random() < 0.5) throw new RuntimeException("Fail");
                })
                .build();

        Job jobC = new Job.Builder()
                .id("C")
                .priority(1)
                .strategy(new CronStrategy(5))
                .command(() -> System.out.println("Cron Job C executed"))
                .build();

        scheduler.submit(jobA);
        scheduler.submit(jobB);
        scheduler.submit(jobC);

        scheduler.start();
    }
}