package org.example.strategy.jobListenerStrategy;

import org.example.models.Job;

public class MetricsListener implements JobListener{

    @Override
    public void onStart(Job job) {
        System.out.println("[METRIC] Job started: " + job.getId());
    }

    @Override
    public void onSuccess(Job job) {
        System.out.println("[METRIC] Job success: " + job.getId());
    }

    @Override
    public void onFailure(Job job, Exception e) {
        System.out.println("[METRIC] Job failed: " + job.getId());
    }
}
