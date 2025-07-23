package io.nexusflow.eventschemas;

public class TaskCompletionEvent {

    private Long taskRunId;
    private Long workflowRunId;
    private String status;
    private String logsUrl;
    private String artifactUrl;
    private String message;

    public TaskCompletionEvent() {
    }
    public TaskCompletionEvent(Long taskRunId, Long workflowRunId, String status, String logsUrl, String artifactUrl, String message) {
        this.taskRunId = taskRunId;
        this.workflowRunId = workflowRunId;
        this.status = status;
        this.logsUrl = logsUrl;
        this.artifactUrl = artifactUrl;
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

    public String getLogsUrl() {
        return logsUrl;
    }

    public void setLogsUrl(String logsUrl) {
        this.logsUrl = logsUrl;
    }

    public String getArtifactUrl() {
        return artifactUrl;
    }

    public void setArtifactUrl(String artifactUrl) {
        this.artifactUrl = artifactUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TaskCompletionEvent{" +
                "taskRunId=" + taskRunId +
                ", workflowRunId=" + workflowRunId +
                ", status='" + status + '\'' +
                ", logsUrl='" + logsUrl + '\'' +
                ", artifactUrl='" + artifactUrl + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
