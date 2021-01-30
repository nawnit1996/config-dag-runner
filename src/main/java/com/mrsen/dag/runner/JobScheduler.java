package com.mrsen.dag.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mrsen.dag.runner.ExecutionDependencyResolver.*;


/**
 * 1. Takes care of creating DAG.
 * 2. Submit each stage with maximum possible task that can be executed in parallel independently.
 * 3. Keep Track of stageFailures and retries only for task which has failed in a stage.
 * 4. In case of multiple failures job can fail and store meta data of intermediate
 * stage and in next Run it would run only pending stages.
 */
public class JobScheduler {
    private TaskScheduler taskScheduler;
    private DAG dag;
    private String jsonConfig;

    public JobScheduler(TaskScheduler scheduler, String jsonConfig) {
        this.taskScheduler = scheduler;
        this.jsonConfig = jsonConfig;
        createDag();
    }

    private void createDag() {
        Map<String, Object> stageMap = Util.getMapFromJson(jsonConfig);

        Map<String, Object> stages = (Map<String, Object>) stageMap.get("stages");
        final Set<String> stageNames = stages.keySet();

        /**Create Dependency Map**/
        Map<String, List<String>> stageDependencyMap = new HashMap<>();
        getDependencyMap(stages, stageNames, stageDependencyMap);

        /**Create Stage execution order**/
        List<Set<String>> executionOrder = new ArrayList<>();
        final Set<String> firstStage = getFirstExecution(stageDependencyMap);
        populateSubsequentExecution(stageDependencyMap, firstStage, executionOrder);
        this.dag = new DAG(executionOrder, stages);
        this.dag.createTasks();
    }


    public void explain(boolean detailedPlan) {
        if (detailedPlan) {
            showDetailedPlan();
        } else {
            showMinimalPlan();
        }
    }

    private void showMinimalPlan() {
        AtomicInteger integer = new AtomicInteger(1);
        final StringBuilder stringBuilder = new StringBuilder("*************************\n");
        this.dag.getStageExecutionOrder().forEach(x -> {
            stringBuilder.append(String.valueOf("<" + integer.getAndAdd(1) + ">" + "." + x.stream().collect(Collectors.joining("||")).toString() + "\n"));
        });
        stringBuilder.append("*************************");
        System.err.println(stringBuilder);
    }

    public void showDetailedPlan() {
        AtomicInteger integer = new AtomicInteger(1);
        final StringBuilder stringBuilder = new StringBuilder("*************************\n");
        this.dag.getStageExecutionOrder().forEach(x -> {
            stringBuilder.append("------------------------------------------------------\n");
            stringBuilder.append("<" + integer.getAndAdd(1) + ">" + "." + x.stream().collect(Collectors.joining("||")).toString() + "\n");
            stringBuilder.append("------------------------------------------------------\n");

            x.forEach(st -> appendTaskPlan(stringBuilder, st));
            stringBuilder.append("======================================================\n");

        });
        stringBuilder.append("*************************");
        System.err.println(stringBuilder);
    }

    public void appendTaskPlan(StringBuilder stringBuilder, String stageName) {
        stringBuilder.append("\n[" + stageName + "]\n");
        final List<Set<Task>> tasksForStages = dag.getTasksForStages(stageName);
        AtomicInteger integer = new AtomicInteger(1);

        tasksForStages.forEach(tasks -> {
            stringBuilder.append("T." + integer.getAndAdd(1) + " : " + tasks.stream().map(task -> task.toString()).collect(Collectors.joining("||")).toString() + "\n");
        });

    }

    public void executeDag() {

        while (dag.hasPendingStages()) {

            final Set<String> nextStageToExecute = dag.getNextStageToExecute();

            nextStageToExecute.parallelStream().forEach(stageName -> {
                this.taskScheduler.submitTask(this.dag.getTasksForStages(stageName));
            });
        }
    }

}
