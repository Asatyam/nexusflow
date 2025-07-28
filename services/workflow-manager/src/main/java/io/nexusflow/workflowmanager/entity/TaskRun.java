package io.nexusflow.workflowmanager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class TaskRun {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_run_id", nullable = false)
    @JsonBackReference
    private WorkflowRun workflowRun;

    private String taskName;
    private String status;
    private Integer retries=0;
    private String logsUrl;
    private String artifactUrl;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TaskRun() {
    }

    public TaskRun(Long id, WorkflowRun workflowRun, String taskName, String status, Integer retries, String logUrl, String artifactUrl, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.workflowRun = workflowRun;
        this.taskName = taskName;
        this.status = status;
        this.retries = retries;
        this.logsUrl = logUrl;
        this.artifactUrl = artifactUrl;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public String getLogsUrl() {
        return logsUrl;
    }

    public void setLogsUrl(String logUrl) {
        this.logsUrl = logUrl;
    }

    public String getArtifactUrl() {
        return artifactUrl;
    }

    public void setArtifactUrl(String artifactUrl) {
        this.artifactUrl = artifactUrl;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TaskRun{" +
                "id=" + id +
                ", workflowRun=" + workflowRun +
                ", taskName='" + taskName + '\'' +
                ", status=" + status +
                ", retries=" + retries +
                ", logUrl='" + logsUrl + '\'' +
                ", artifactUrl='" + artifactUrl + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
