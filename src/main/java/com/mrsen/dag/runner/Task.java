package com.mrsen.dag.runner;

/**
 * Represents a task in a stage.
 */
public class Task {
    TaskType taskType;
    Object properties;
    String taskId;

    Task(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "Task-" + taskId;
    }

    void execute() {
        System.err.println("Executing task : " + taskId);
    }
}
