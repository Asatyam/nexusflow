package io.nexusflow.workflowmanager.listener;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import io.nexusflow.workflowmanager.domain.WorkflowGraph;
import io.nexusflow.workflowmanager.entity.TaskRun;
import io.nexusflow.workflowmanager.entity.WorkflowRun;
import io.nexusflow.workflowmanager.repositories.TaskRunRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowRunRepository;
import io.nexusflow.workflowmanager.service.WorkflowDefinitionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TaskResultListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskResultListener.class);

    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;
    private final WorkflowDefinitionParser workflowDefinitionParser;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public TaskResultListener(WorkflowRunRepository workflowRunRepository, TaskRunRepository taskRunRepository, WorkflowDefinitionParser workflowDefinitionParser, KafkaTemplate<String, Object> kafkaTemplate) {
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
        this.workflowDefinitionParser = workflowDefinitionParser;
        this.kafkaTemplate = kafkaTemplate;
    }

    private void scheduleNewTask(WorkflowRun workflowRun, String taskName) {
        if(taskRunRepository.existsByWorkflowRunAndTaskName(workflowRun, taskName)) {
            LOGGER.info("Task {} is already scheduled for workflow run {}", taskName, workflowRun.getId());
            return;
        }
        LOGGER.info("Scheduling new task {} for workflow run {}", taskName, workflowRun.getId());

        TaskRun taskRun = new TaskRun();
        taskRun.setTaskName(taskName);
        taskRun.setWorkflowRun(workflowRun);
        taskRun.setStatus("SCHEDULED");
        TaskRun savedTaskRun = taskRunRepository.save(taskRun);

        sendTaskExecutionEvent(savedTaskRun);

    }

    private void sendTaskExecutionEvent(TaskRun taskRun) {
        TaskExecutionEvent taskExecutionEvent = new TaskExecutionEvent();
        taskExecutionEvent.setTaskRunId(taskRun.getId());
        taskExecutionEvent.setWorkflowRunId(taskRun.getWorkflowRun().getId());
        taskExecutionEvent.setTaskName(taskRun.getTaskName());
        LOGGER.info("Publishing task execution event for task: {} in workflow run: {}", taskExecutionEvent.getTaskName(), taskExecutionEvent.getWorkflowRunId());
        kafkaTemplate.send("tasks.execute", taskRun.getWorkflowRun().getId().toString(), taskExecutionEvent);
    }

    private void scheduleNextTasks(TaskRun completedTaskRun, WorkflowRun workflowRun, WorkflowGraph workflowGraph) {
        List<String> dependentTaskNames = workflowGraph.getGraph().get(completedTaskRun.getTaskName());
        if (dependentTaskNames.isEmpty()) {
            LOGGER.info("No dependent tasks found for task: {}", completedTaskRun.getTaskName());
            return;
        }
        Set<String> completedTaskRuns = taskRunRepository.findByWorkflowRunIdAndStatus(workflowRun.getId(), "COMPLETED")
                .stream()
                .map(TaskRun::getTaskName)
                .collect(Collectors.toSet());
        // Due to Transactional, the current task run will not be in the completed set
        completedTaskRuns.add(completedTaskRun.getTaskName());

        for(String taskName : dependentTaskNames) {
            List<String> prerequisites = workflowGraph.getTaskNodeMap().get(taskName).getDependsOn();
            boolean canSchedule = completedTaskRuns.containsAll(prerequisites);
            if (canSchedule) {
                scheduleNewTask(workflowRun, taskName);
            }
            else {
                LOGGER.info("Skipping task {} as prerequisites {} are not completed", taskName, prerequisites);
            }
        }

    }

    private void checkForWorkflowCompletion(WorkflowRun workflowRun, WorkflowGraph workflowGraph) {
        long totalTasksInGraph = workflowGraph.getGraph().size();
        long completedTasks = taskRunRepository.countByWorkflowRunAndStatus(workflowRun, "COMPLETED");
        if (completedTasks == totalTasksInGraph) {
            LOGGER.info("All tasks completed for workflow run: {}", workflowRun.getId());
            workflowRun.setStatus("COMPLETED");
            workflowRun.setEndTime(LocalDateTime.now());
        } else {
            LOGGER.info("Workflow run {} is still running. Completed tasks: {}/{}", workflowRun.getId(), completedTasks, totalTasksInGraph);
        }
    }

    @Transactional
    @KafkaListener(topics = "tasks.results")
    public void listenForTaskResults(TaskCompletionEvent event) {
        LOGGER.info("Received task completion event: {}", event);

        TaskRun completedTaskRun = taskRunRepository.findById(event.getTaskRunId())
                .orElseThrow(()->new RuntimeException("TaskRun not found for ID: " + event.getTaskRunId()));

        if (event.getStatus().equals("FAILURE")) {
            LOGGER.error("Task {} failed with message: {}", completedTaskRun.getTaskName(), event.getMessage());
            completedTaskRun.setStatus("FAILED");
            completedTaskRun.setEndTime(LocalDateTime.now());
            WorkflowRun workflowRun = completedTaskRun.getWorkflowRun();
            workflowRun.setStatus("FAILED");
            workflowRun.setEndTime(LocalDateTime.now());
            LOGGER.info("The WorkflowRun {} has failed due to task failure of {}", workflowRun.getId(), completedTaskRun.getTaskName());
        } else if (event.getStatus().equals("COMPLETED") || event.getStatus().equals("SUCCESS")) {
            LOGGER.info("Task {} completed successfully", completedTaskRun.getTaskName());
            completedTaskRun.setStatus("COMPLETED");
            completedTaskRun.setLogsUrl(event.getLogsUrl());
            completedTaskRun.setArtifactUrl(event.getArtifactUrl());
            completedTaskRun.setEndTime(LocalDateTime.now());

            LOGGER.info("RECEIVED THE MESSAGE: {}", event.getMessage());


            WorkflowRun workflowRun = completedTaskRun.getWorkflowRun();
            //TODO: Optimize using Cache
            WorkflowGraph workflowGraph = workflowDefinitionParser.buildWorkflowGraph(workflowRun.getWorkflowDefinition());
            scheduleNextTasks(completedTaskRun, workflowRun, workflowGraph);

            checkForWorkflowCompletion(workflowRun, workflowGraph);
        } else {
            LOGGER.warn("Received task completion event with unknown status: {}", event.getStatus());
        }
    }
}
