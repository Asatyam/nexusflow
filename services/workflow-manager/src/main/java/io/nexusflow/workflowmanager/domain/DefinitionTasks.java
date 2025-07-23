package io.nexusflow.workflowmanager.domain;

import java.util.List;

public class DefinitionTasks {

    private List<TaskNode> tasks;

    public DefinitionTasks() {
    }

    public DefinitionTasks(List<TaskNode> taskNodes) {
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
        return "DefinitionTasks{" +
                "tasks=" + tasks +
                '}';
    }
}
