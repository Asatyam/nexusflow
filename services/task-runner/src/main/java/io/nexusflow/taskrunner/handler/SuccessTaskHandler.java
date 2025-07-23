package io.nexusflow.taskrunner.handler;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuccessTaskHandler implements TaskHandler {

    private static final String TASK_NAME = "success-task";
    private static final Logger LOGGER = LoggerFactory.getLogger(SuccessTaskHandler.class);

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public TaskCompletionEvent execute(TaskExecutionEvent event) {
        LOGGER.info("Running a successful task: {}", event.getTaskName());
        TaskCompletionEvent taskCompletionEvent = new TaskCompletionEvent();
        taskCompletionEvent.setTaskRunId(event.getTaskRunId());
        taskCompletionEvent.setWorkflowRunId(event.getWorkflowRunId());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Task execution interrupted: {}", e.getMessage());
            taskCompletionEvent.setStatus("FAILURE");
            taskCompletionEvent.setMessage("Task execution was interrupted.");
            return taskCompletionEvent;
        }
        LOGGER.info("Task {} executed successfully", event.getTaskName());

        taskCompletionEvent.setStatus("SUCCESS");
        taskCompletionEvent.setMessage("Task executed successfully.");
        taskCompletionEvent.setLogsUrl("https://example.com/logs/" + event.getTaskRunId());
        taskCompletionEvent.setArtifactUrl("https://example.com/artifacts/" + event.getTaskRunId());

        return taskCompletionEvent;
    }
}
