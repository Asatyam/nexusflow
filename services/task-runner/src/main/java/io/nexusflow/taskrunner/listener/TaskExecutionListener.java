package io.nexusflow.taskrunner.listener;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutionListener.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public TaskExecutionListener(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics="tasks.execute")
    public void listenForTasks(TaskExecutionEvent taskExecutionEvent) {
        LOGGER.info("Received task execution event: {}", taskExecutionEvent);

        try{
            LOGGER.info("Executing task: {}...", taskExecutionEvent.getTaskName());
            Thread.sleep(3000);
            LOGGER.info("Task {} executed successfully", taskExecutionEvent.getTaskName());
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            LOGGER.error("Task execution interrupted: {}", e.getMessage());
        }

        TaskCompletionEvent taskCompletionEvent = new TaskCompletionEvent();
        taskCompletionEvent.setStatus("COMPLETED");
        taskCompletionEvent.setWorkflowRunId(taskExecutionEvent.getWorkflowRunId());
        taskCompletionEvent.setTaskRunId(taskExecutionEvent.getTaskRunId());
        taskCompletionEvent.setLogsUrl("http://example.com/logs/" + taskExecutionEvent.getTaskRunId());
        taskCompletionEvent.setArtifactUrl("http://example.com/artifacts/" + taskExecutionEvent.getTaskRunId());

        LOGGER.info("Publishing task completion event: {}", taskCompletionEvent);
        kafkaTemplate.send("tasks.results", taskCompletionEvent.getWorkflowRunId().toString(), taskCompletionEvent);
    }

}
