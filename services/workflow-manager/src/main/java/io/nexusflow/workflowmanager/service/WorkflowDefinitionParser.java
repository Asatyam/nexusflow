package io.nexusflow.workflowmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nexusflow.workflowmanager.domain.DefinitionTasks;
import io.nexusflow.workflowmanager.domain.TaskNode;
import io.nexusflow.workflowmanager.domain.WorkflowGraph;
import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import io.nexusflow.workflowmanager.exception.InvalidWorkflowDefinitionException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkflowDefinitionParser {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private DefinitionTasks parseWorkflowDefinitionTasks(WorkflowDefinition workflowDefinition) {


        DefinitionTasks definitionTasks;
        String definition = workflowDefinition.getDefinition();
        try {
            definitionTasks = objectMapper.readValue(definition, DefinitionTasks.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing workflow definition: " + e.getMessage());
        }
        return definitionTasks;
    }

    private Map<String, TaskNode> buildMapFromTasks(DefinitionTasks definitionTasks) {
        return definitionTasks.getTasks().stream().collect(Collectors.toMap(TaskNode::getName, Function.identity()));
    }

    private Map<String, List<String>> buildGraphFromTasks(DefinitionTasks definitionTasks) {
        List<TaskNode> tasks = definitionTasks.getTasks();
        Map<String, List<String>> graph = new HashMap<>();
        for(TaskNode task : tasks) {
            graph.computeIfAbsent(task.getName(), k -> new ArrayList<>());
            List<String> dependencies = task.getDependsOn();
            for(String dependency: dependencies) {
                graph.computeIfAbsent(dependency, k -> new ArrayList<>()).add(task.getName());
            }
        }
        return graph;
    }

    // This method validates the DAG (Directed Acyclic Graph) of the workflow using Kahn's algorithm.
    public void detectCycle (WorkflowDefinition workflowDefinition) {

        DefinitionTasks definitionTasks = parseWorkflowDefinitionTasks(workflowDefinition);
        Map<String, List<String>> graph = buildGraphFromTasks(definitionTasks);

        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> inDegree = new HashMap<>();

        long visited = 0;

        for(String node : graph.keySet()) {
            inDegree.put(node, 0);
        }
        for(Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String node = entry.getKey();
            for(String dependency : entry.getValue()) {
                inDegree.put(dependency, inDegree.getOrDefault(dependency, 0) + 1);
            }
        }
        for(Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }
        while(!queue.isEmpty()) {
            visited++;
            String currentNode = queue.poll();
            List<String> neighbors = graph.get(currentNode);
            for(String neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        // If there are still nodes with non-zero in-degree, then the graph has a cycle.
        if(visited != graph.size()) {
            throw new InvalidWorkflowDefinitionException("Workflow definition contains a cycle");
        }
    }
    private List<TaskNode> getStartingNodes(DefinitionTasks definitionTasks) {

        List<TaskNode> startingNodes = new ArrayList<>();
        for(TaskNode task : definitionTasks.getTasks()) {
            if (task.getDependsOn() == null || task.getDependsOn().isEmpty()) {
                startingNodes.add(task);
            }
        }
        return startingNodes;

    }
    public WorkflowGraph buildWorkflowGraph(WorkflowDefinition workflowDefinition) {
        DefinitionTasks definitionTasks = parseWorkflowDefinitionTasks(workflowDefinition);
        Map<String, TaskNode> taskNodeMap = buildMapFromTasks(definitionTasks);
        Map<String, List<String>> graph = buildGraphFromTasks(definitionTasks);
        List<TaskNode> startingNodes = getStartingNodes(definitionTasks);

        return new WorkflowGraph(taskNodeMap, graph, startingNodes);
    }

}
