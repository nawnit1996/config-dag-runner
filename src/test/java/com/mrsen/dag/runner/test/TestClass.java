package com.mrsen.dag.runner.test;


import com.mrsen.dag.runner.JobScheduler;
import com.mrsen.dag.runner.TaskScheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TestClass {
    public static String STAGE_CONF = "src/test/resources/steps.json";

    public static void main(String[] args) throws IOException {

        final String jsonStr = new String(Files.readAllBytes(Paths.get(STAGE_CONF)));


        final JobScheduler jobScheduler = new JobScheduler(new TaskScheduler(),jsonStr);

        jobScheduler.explain(true);
        jobScheduler.executeDag();

    }
}
