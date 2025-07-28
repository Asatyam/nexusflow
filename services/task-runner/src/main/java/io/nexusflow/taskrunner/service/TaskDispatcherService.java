package io.nexusflow.taskrunner.service;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import io.nexusflow.taskrunner.handler.TaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskDispatcherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDispatcherService.class);
    private final Map<String, TaskHandler> taskHandlers;

    public TaskDispatcherService(List<TaskHandler> taskHandlers) {
        this.taskHandlers = taskHandlers.stream().collect(Collectors.toMap(TaskHandler::getTaskName, Function.identity()));
    }

    public TaskCompletionEvent dispatch(TaskExecutionEvent event) {

        LOGGER.info("Executing task: {}...", event.getTaskName());

        TaskHandler taskHandler = taskHandlers.get(event.getTaskName());
        TaskCompletionEvent taskCompletionEvent;

        if (taskHandler == null) {
            LOGGER.error("No handler found for task: {}", event.getTaskName());
            return createFailureEvent(event, "No handler found for task: " + event.getTaskName());
        }
        try{
            return taskHandler.execute(event);
        }catch (Exception e){
            LOGGER.error("Error executing task {}: {}", event.getTaskName(), e.getMessage());
            return createFailureEvent(event, "Task execution failed: " + e.getMessage());
        }
    }

    private TaskCompletionEvent createFailureEvent(TaskExecutionEvent event, String message) {
        TaskCompletionEvent taskCompletionEvent = new TaskCompletionEvent();
        taskCompletionEvent.setTaskRunId(event.getTaskRunId());
        taskCompletionEvent.setWorkflowRunId(event.getWorkflowRunId());
        taskCompletionEvent.setStatus("FAILURE");
        taskCompletionEvent.setMessage(message);
        return taskCompletionEvent;
    }

}
