package org.example.models;

import org.example.strategy.jobCommand.JobCommand;
import org.example.strategy.retryPolicyStrategy.RetryPolicy;
import org.example.strategy.schedulingStrategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;

public class Job {
    String id;
    int priority;
    JobCommand command;
    RetryPolicy retryPolicy;
    SchedulingStrategy schedulingStrategy;
    List<String> dependencies;
    long nextExecutionTime;

    private Job(Builder b) {
        this.id = b.id;
        this.priority = b.priority;
        this.command = b.command;
        this.schedulingStrategy = b.strategy;
        this.retryPolicy = b.retryPolicy;
        this.dependencies = b.dependencies;
    }

    public static class Builder {
        private String id;
        private int priority;
        private JobCommand command;
        private SchedulingStrategy strategy;
        private RetryPolicy retryPolicy;
        private List<String> dependencies = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder priority(int p) { this.priority = p; return this; }
        public Builder command(JobCommand c) { this.command = c; return this; }
        public Builder strategy(SchedulingStrategy s) { this.strategy = s; return this; }
        public Builder retry(RetryPolicy r) { this.retryPolicy = r; return this; }
        public Builder dependsOn(List<String> d) { this.dependencies = d; return this; }

        public Job build() {
            return new Job(this);
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public JobCommand getCommand() {
        return command;
    }

    public void setCommand(JobCommand command) {
        this.command = command;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public SchedulingStrategy getSchedulingStrategy() {
        return schedulingStrategy;
    }

    public void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }
}
