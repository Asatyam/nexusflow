package io.nexusflow.workflowmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nexusflow.workflowmanager.domain.DefinitionTasks;
import io.nexusflow.workflowmanager.domain.TaskNode;
import io.nexusflow.workflowmanager.domain.WorkflowGraph;
import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkflowDefinitionParser {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public DefinitionTasks parseWorkflowDefinitionTasks(WorkflowDefinition workflowDefinition) {


        DefinitionTasks definitionTasks;
        String definition = workflowDefinition.getDefinition();
        try {
            definitionTasks = objectMapper.readValue(definition, DefinitionTasks.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing workflow definition: " + e.getMessage());
        }
        return definitionTasks;
    }

    public Map<String, TaskNode> buildMapFromTasks(DefinitionTasks definitionTasks) {
        return definitionTasks.getTasks().stream().collect(Collectors.toMap(TaskNode::getName, Function.identity()));
    }

    public Map<String, List<String>> buildGraphFromTasks(DefinitionTasks definitionTasks) {
        List<TaskNode> tasks = definitionTasks.getTasks();
        Map<String, List<String>> graph = new HashMap<>();
        for(TaskNode task : tasks) {
            graph.computeIfAbsent(task.getName(), k -> new ArrayList<>());
            List<String> dependencies = task.getDependsOn();
            for(String dependency: dependencies) {
                graph.get(dependency).add(task.getName());
            }
        }
        return graph;
    }

    public List<TaskNode> getStartingNodes(DefinitionTasks definitionTasks) {

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
