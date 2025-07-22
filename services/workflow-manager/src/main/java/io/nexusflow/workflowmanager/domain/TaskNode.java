package io.nexusflow.workflowmanager.domain;

import java.util.List;

public class TaskNode {

    private String name;
    private List<String> dependsOn;

    public TaskNode() {
    }

    public TaskNode(String name, List<String> dependencies) {
        this.name = name;
        this.dependsOn = dependencies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependencies) {
        this.dependsOn = dependencies;
    }

    @Override
    public String toString() {
        return "TaskNode{" +
                "name='" + name + '\'' +
                ", dependsOn=" + dependsOn +
                '}';
    }
}
