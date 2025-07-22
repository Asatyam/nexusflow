package io.nexusflow.workflowmanager.listener;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.workflowmanager.repositories.TaskRunRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskResultListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskResultListener.class);
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;

    @Autowired
    public TaskResultListener(WorkflowRunRepository workflowRunRepository, TaskRunRepository taskRunRepository) {
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
    }

    @KafkaListener(topics = "tasks.results")
    public void listenForTaskResults(TaskCompletionEvent event) {
        LOGGER.info("Received task completion event: {}", event);

        taskRunRepository.findById(event.getTaskRunId()).ifPresent(taskRun -> {
            taskRun.setStatus(event.getStatus());
            taskRun.setLogsUrl(event.getLogsUrl());
            taskRun.setArtifactUrl(event.getArtifactUrl());
            taskRun.setEndTime(LocalDateTime.now());

            workflowRunRepository.findById(event.getTaskRunId()).ifPresent(workflowRun -> {
                workflowRun.setStatus("COMPLETED");
                workflowRun.setEndTime(LocalDateTime.now());
                workflowRunRepository.save(workflowRun);
                LOGGER.info("Updated workflow run status to {} for workflowRunId: {}", workflowRun.getStatus(),workflowRun.getId());
            });
            taskRunRepository.save(taskRun);
        });

    }
}
