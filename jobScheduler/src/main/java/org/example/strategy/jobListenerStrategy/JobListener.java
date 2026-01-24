package org.example.strategy.jobListenerStrategy;

import org.example.models.Job;

public interface JobListener {
    void onStart(Job job);
    void onSuccess(Job job);
    void onFailure(Job job, Exception e);
}
