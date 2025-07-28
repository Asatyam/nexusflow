package io.nexusflow.taskrunner.handler;

import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;

public interface TaskHandler {
    String getTaskName();
    TaskCompletionEvent execute(TaskExecutionEvent event);
}
