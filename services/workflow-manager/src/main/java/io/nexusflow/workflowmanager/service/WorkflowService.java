package io.nexusflow.workflowmanager.service;

import io.nexusflow.eventschemas.TaskExecutionEvent;
import io.nexusflow.workflowmanager.domain.TaskNode;
import io.nexusflow.workflowmanager.domain.WorkflowGraph;
import io.nexusflow.workflowmanager.entity.TaskRun;
import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import io.nexusflow.workflowmanager.entity.WorkflowRun;
import io.nexusflow.workflowmanager.repositories.TaskRunRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowDefinitionRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Service
public class WorkflowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowService.class);
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WorkflowDefinitionParser workflowDefinitionParser;


    @Autowired
    public WorkflowService(WorkflowDefinitionRepository workflowDefinitionRepository, WorkflowRunRepository workflowRunRepository, TaskRunRepository taskRunRepository, KafkaTemplate<String, Object> kafkaTemplate, WorkflowDefinitionParser workflowDefinitionParser) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.workflowDefinitionParser = workflowDefinitionParser;
    }



    public WorkflowDefinition createWorkflow(String name,String description, String definition) {
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setName(name);
        workflowDefinition.setDefinition(definition);
        workflowDefinition.setDescription(description);
        workflowDefinition.setVersion(1);
        workflowDefinition.setEnabled(true);

        workflowDefinitionParser.detectCycle(workflowDefinition);

        return workflowDefinitionRepository.save(workflowDefinition);
    }
    
    private WorkflowRun saveStartingTaskRuns(WorkflowRun workflowRun, WorkflowGraph graph) {

        for (TaskNode node : graph.getStartingNodes()) {
            TaskRun taskRun = new TaskRun();
            taskRun.setWorkflowRun(workflowRun);
            taskRun.setTaskName(node.getName());
            taskRun.setStatus("PENDING");
            workflowRun.getTaskRuns().add(taskRun);
        }
        return workflowRunRepository.save(workflowRun);
    }

    private void runAndSchedulePendingTasks(WorkflowRun workflowRunSaved) {

        for (TaskRun taskRun : workflowRunSaved.getTaskRuns()) {
            if(taskRun.getStatus().equals("PENDING")) {
                TaskExecutionEvent taskExecutionEvent = new TaskExecutionEvent();
                taskExecutionEvent.setTaskRunId(taskRun.getId());
                taskExecutionEvent.setWorkflowRunId(workflowRunSaved.getId());
                taskExecutionEvent.setTaskName(taskRun.getTaskName());
                taskRun.setStatus("SCHEDULED");
                LOGGER.info("Scheduling task {} for workflow run {}", taskRun.getTaskName(), workflowRunSaved.getId());
                kafkaTemplate.send("tasks.execute", workflowRunSaved.getId().toString(), taskExecutionEvent);
                taskRunRepository.save(taskRun);
            }
        }
    }


    @Transactional
    public WorkflowRun runWorkflow(Long WorkflowDefinitionId) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(WorkflowDefinitionId)
                .orElseThrow(() -> new RuntimeException("WorkflowDefinition not found"));

        WorkflowGraph workflowGraph = workflowDefinitionParser.buildWorkflowGraph(workflowDefinition);
        LOGGER.debug("Workflow Graph: {}", workflowGraph);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflowDefinition(workflowDefinition);
        workflowRun.setStatus("RUNNING");
        workflowRun.setStartTime(LocalDateTime.now());

        workflowRun.setTaskRuns(new ArrayList<>());
        
        WorkflowRun workflowRunSaved = saveStartingTaskRuns(workflowRun, workflowGraph);
        runAndSchedulePendingTasks(workflowRunSaved);

        return workflowRunSaved;
    }



}
