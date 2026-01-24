package org.example.service;

import org.example.models.Job;

import java.util.Set;

public class DependencyResolver {

    public boolean canExecute(Job job, Set<String> completed){
        for(String dependency : job.getDependencies()){
            if(!completed.contains(dependency)) {
                return false;
            }
        }
        return true;
    }
}
