# config-dag-runner
Project which takes steps json as input and create a DAG to executes.

It Can execute independent  steps in parallel. And inside each stages independent tasks can also be executes parallely.

explain() in DAGScheduelr will show you detailed plan of the DAG created. It shows Stage which will be executed parallely and inside each stage what all task can be executed parallely.

Steps.json is sample json respresnting steps to be exeuted.Steps written doesn't has to be in order, it creates depndency tree on its own and decides the order in which steps can be executed.
https://github.com/nawnit1996/config-dag-runner/blob/main/src/test/resources/steps.json

TestClass.java is a test program which created DAG from above test steps and Create a DAG and shows plan and execute. 
https://github.com/nawnit1996/config-dag-runner/blob/main/src/test/java/com/mrsen/dag/runner/test/TestClass.java
