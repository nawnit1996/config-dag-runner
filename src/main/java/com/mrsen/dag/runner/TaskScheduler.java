package com.mrsen.dag.runner;

import java.util.List;
import java.util.Set;

/**
 * Run task of a stages and also stores meta data about executing task.
 */
public class TaskScheduler {
    public void submitTask(final List<Set<Task>> tasksForStages) {
        tasksForStages.forEach(x -> {
            executeInparallel(x);
        });
    }

    private void executeInparallel(final Set<Task> x) {
        x.parallelStream().forEach(Task::execute);
    }
}
