package com.mrsen.dag.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mrsen.dag.runner.ExecutionDependencyResolver.*;

/**
 * Represents Execution Plan for JOb.
 */
public class DAG {
    List<Set<String>> stageExecutionOrder;
    Map<String, Object> inputStages;
    Map<String, List<Set<Task>>> stagesTaskMap;
    int currentExecutionNumber = -1;

    DAG(List<Set<String>> executionOrder, Map<String, Object> inputStages) {
        this.stageExecutionOrder = executionOrder;
        this.inputStages = inputStages;
    }

    public List<Set<String>> getStageExecutionOrder() {
        return stageExecutionOrder;
    }

    public boolean hasPendingStages() {
        return currentExecutionNumber<stageExecutionOrder.size()-1;
    }

    Set<String> getNextStageToExecute() {
        return this.stageExecutionOrder.get(++currentExecutionNumber);
    }
    List<Set<Task>> getTasksForStages(String stageName){
        return this.stagesTaskMap.get(stageName);
    }

    public void createTasks() {
        if (stagesTaskMap != null) {
            return;
        }
        stagesTaskMap = new HashMap<>();
        inputStages.forEach((k, v) -> {
            Map<String, Object> stage = (Map<String, Object>) (v);
            String taskKeyName = (String) stage.get("key");
            Map<String, Object> tasks = (Map<String, Object>) stage.get(taskKeyName);
            final Set<String> stageNames = tasks.keySet();

            /**Create Dependency Map**/
            Map<String, List<String>> taskDependencyMap = new HashMap<>();
            getDependencyMap(tasks, stageNames, taskDependencyMap);

            /**Create Stage execution order**/
            List<Set<String>> taskExecutionOrder = new ArrayList<>();
            final Set<String> firstStage = getFirstExecution(taskDependencyMap);
            populateSubsequentExecution(taskDependencyMap, firstStage, taskExecutionOrder);

            final List<Set<Task>> collect = taskExecutionOrder.stream().map(t -> t.stream().map(taskName -> new Task(k+"_"+taskName)).collect(Collectors.toSet())).collect(Collectors.toList());
            this.stagesTaskMap.put(k, collect);
        });
    }
}
