package io.nexusflow.eventschemas;

public class TaskExecutionEvent {

    private Long taskRunId;
    private Long workflowRunId;
    private String status;
    private String message;

    public TaskExecutionEvent() {
    }

    public TaskExecutionEvent(Long taskRunId, Long workflowRunId, String status, String message) {
        this.taskRunId = taskRunId;
        this.workflowRunId = workflowRunId;
        this.status = status;
        this.message = message;
    }

    public Long getTaskRunId() {
        return taskRunId;
    }

    public void setTaskRunId(Long taskRunId) {
        this.taskRunId = taskRunId;
    }

    public Long getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Long workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TaskExecutionEvent{" +
                "taskRunId=" + taskRunId +
                ", workflowRunId=" + workflowRunId +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
