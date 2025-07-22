package io.nexusflow.workflowmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import io.nexusflow.workflowmanager.domain.WorkflowGraph;
import io.nexusflow.workflowmanager.entity.TaskRun;
import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import io.nexusflow.workflowmanager.entity.WorkflowRun;
import io.nexusflow.workflowmanager.repositories.TaskRunRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowDefinitionRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkflowService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Autowired
    public WorkflowService(WorkflowDefinitionRepository workflowDefinitionRepository, WorkflowRunRepository workflowRunRepository, TaskRunRepository taskRunRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public WorkflowGraph parseWorkflowDefinition(WorkflowDefinition workflowDefinition) {

        WorkflowGraph workflowGraph = new WorkflowGraph();
        String definition = workflowDefinition.getDefinition();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            workflowGraph = objectMapper.readValue(definition, WorkflowGraph.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing workflow definition: " + e.getMessage());
        }
        return workflowGraph;
    }

    public WorkflowDefinition createWorkflow(String name,String description, String definition) {
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setName(name);
        workflowDefinition.setDefinition(definition);
        workflowDefinition.setDescription(description);
        workflowDefinition.setVersion(1);
        workflowDefinition.setEnabled(true);
        return workflowDefinitionRepository.save(workflowDefinition);
    }

    public WorkflowRun runWorkflow(Long WorkflowDefinitionId) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(WorkflowDefinitionId)
                .orElseThrow(() -> new RuntimeException("WorkflowDefinition not found"));

        WorkflowGraph workflowGraph = parseWorkflowDefinition(workflowDefinition);
        System.out.println("Workflow Graph: " + workflowGraph);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflowDefinition(workflowDefinition);
        workflowRun.setStatus("RUNNING");
        workflowRun.setStartTime(LocalDateTime.now());
        WorkflowRun workflowRunSaved = workflowRunRepository.save(workflowRun);

        TaskRun taskRun = new TaskRun();
        taskRun.setWorkflowRun(workflowRunSaved);
        //TODO: Parse task name from workflow definition
        taskRun.setTaskName("initial-task");
        taskRun.setStatus("PENDING");
        taskRun.setStartTime(LocalDateTime.now());

        TaskRun taskRunSaved = taskRunRepository.save(taskRun);
        TaskExecutionEvent taskExecutionEvent = new TaskExecutionEvent(taskRunSaved.getId(),workflowRunSaved.getId(), taskRunSaved.getTaskName() );
        kafkaTemplate.send("tasks.execute", workflowRunSaved.getId().toString(), taskExecutionEvent);

        return workflowRunSaved;
    }

}
