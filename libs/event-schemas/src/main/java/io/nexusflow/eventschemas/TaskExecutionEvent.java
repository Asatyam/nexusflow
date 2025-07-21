package io.nexusflow.eventschemas;

public class TaskExecutionEvent {

    private Long taskRunId;
    private Long workflowRunId;
    private String taskName;

    public TaskExecutionEvent() {
    }

    public TaskExecutionEvent(Long taskRunId, Long workflowRunId, String taskName) {
        this.taskRunId = taskRunId;
        this.workflowRunId = workflowRunId;
        this.taskName = taskName;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "TaskExecutionEvent{" +
                "taskRunId=" + taskRunId +
                ", workflowRunId=" + workflowRunId +
                ", taskName='" + taskName + '\'' +
                '}';
    }
}
