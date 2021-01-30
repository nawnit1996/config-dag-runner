package com.mrsen.dag.runner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resolves task/stage dependecy .
 */
public class ExecutionDependencyResolver {

    public static Set<String> getFirstExecution(Map<String, List<String>> stageDependencyMap) {
        Set<String> initialStage = new HashSet<>();
        stageDependencyMap.forEach((k, v) -> {
            if (v == null) {
                initialStage.add(k);
            }
        });
        return initialStage;
    }

   public static void populateSubsequentExecution(Map<String, List<String>> stageDependencyMap, Set<String> currentStages,List<Set<String>> executionOrder) {
        while (currentStages != null && !currentStages.isEmpty()) {
            executionOrder.add(currentStages);
            currentStages = getNextExecution(stageDependencyMap, currentStages);
        }
    }

    public static Set<String> getNextExecution(Map<String, List<String>> stageDependencyMap, Set<String> currentStages) {
        Set<String> nextStage = new HashSet<>();

        currentStages.forEach(
                curr -> {
                    final Set<String> collect = stageDependencyMap.entrySet().stream().filter(entry ->
                            Objects.nonNull(entry.getValue()) && entry.getValue().contains(curr)
                    ).map(x -> x.getKey()).collect(Collectors.toSet());
                    nextStage.addAll(collect);
                }
        );
        return nextStage;
    }


    public static void getDependencyMap(final Map<String, Object> stages, final Set<String> stageNames, final Map<String, List<String>> stageDependencyMap) {
        stageNames.forEach(stageName -> {
            Map<String, Object> stageDetails = (Map<String, Object>) stages.get(stageName);
            if (stageDetails.get("dependsOn") != null) {
                stageDependencyMap.put(stageName, (List<String>) stageDetails.get("dependsOn"));
            } else {
                stageDependencyMap.put(stageName, null);
            }
        });
    }

}
