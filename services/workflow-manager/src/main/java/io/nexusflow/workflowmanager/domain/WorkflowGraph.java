package io.nexusflow.workflowmanager.domain;

import java.util.List;

public class WorkflowGraph {

    private List<TaskNode> tasks;

    public WorkflowGraph() {
    }

    public WorkflowGraph(List<TaskNode> taskNodes) {
        this.tasks = taskNodes;
    }

    public List<TaskNode> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskNode> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "WorkflowGraph{" +
                "tasks=" + tasks +
                '}';
    }
}
