package io.nexusflow.taskrunner.listener;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import io.nexusflow.taskrunner.service.TaskDispatcherService;
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
    private final TaskDispatcherService taskDispatcherService;

    @Autowired
    public TaskExecutionListener(KafkaTemplate<String, Object> kafkaTemplate, TaskDispatcherService taskDispatcherService) {
        this.kafkaTemplate = kafkaTemplate;
        this.taskDispatcherService = taskDispatcherService;
    }

    @KafkaListener(topics="tasks.execute")
    public void listenForTasks(TaskExecutionEvent taskExecutionEvent) {
        LOGGER.info("Received task execution event: {}", taskExecutionEvent);

        TaskCompletionEvent taskCompletionEvent = taskDispatcherService.dispatch(taskExecutionEvent);
        LOGGER.info("Task completion event: {}", taskCompletionEvent);
        LOGGER.info("Publishing task completion event to Kafka topic: tasks.results");
        kafkaTemplate.send("tasks.results", taskCompletionEvent.getWorkflowRunId().toString(), taskCompletionEvent);
    }

}
