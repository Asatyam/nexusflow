package io.nexusflow.workflowmanager.domain;

import java.util.List;
import java.util.Map;

public class WorkflowGraph {

    private Map<String, TaskNode> taskNodeMap;
    private Map<String, List<String>> graph;
    private List<TaskNode> startingNodes;

    public WorkflowGraph(Map<String, TaskNode> taskNodeMap, Map<String, List<String>> graph, List<TaskNode> startingNodes) {
        this.taskNodeMap = taskNodeMap;
        this.graph = graph;
        this.startingNodes = startingNodes;
    }

    public WorkflowGraph() {
    }

    public Map<String, TaskNode> getTaskNodeMap() {
        return taskNodeMap;
    }

    public void setTaskNodeMap(Map<String, TaskNode> taskNodeMap) {
        this.taskNodeMap = taskNodeMap;
    }

    public Map<String, List<String>> getGraph() {
        return graph;
    }

    public void setGraph(Map<String, List<String>> graph) {
        this.graph = graph;
    }

    public List<TaskNode> getStartingNodes() {
        return startingNodes;
    }

    public void setStartingNodes(List<TaskNode> startingNodes) {
        this.startingNodes = startingNodes;
    }

    @Override
    public String toString() {
        return "WorkflowGraph{" +
                "taskNodeMap=" + taskNodeMap +
                ", graph=" + graph +
                ", startingNodes=" + startingNodes +
                '}';
    }
}
