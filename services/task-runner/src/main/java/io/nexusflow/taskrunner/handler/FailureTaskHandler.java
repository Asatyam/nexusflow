package io.nexusflow.taskrunner.handler;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;

public class FailureTaskHandler implements TaskHandler {
    @Override
    public String getTaskName() {
        return "FailureTaskHandler";
    }

    @Override
    public TaskCompletionEvent execute(TaskExecutionEvent event) {
        TaskCompletionEvent completionEvent = new TaskCompletionEvent();
        completionEvent.setTaskRunId(event.getTaskRunId());
        completionEvent.setStatus("FAILURE");
        completionEvent.setMessage("Task execution failed.");
        return completionEvent;
    }
}
