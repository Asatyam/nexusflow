package io.nexusflow.workflowmanager.entity;

import io.nexusflow.workflowmanager.enums.WorkflowRunStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class WorkflowRun {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private WorkflowDefinition workflowDefinition;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String inputData;
    private String outputData;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "workflowRun", cascade = CascadeType.ALL)
    private List<TaskRun> taskRuns;

    public WorkflowRun() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkflowDefinition getWorkflowDefinition() {
        return workflowDefinition;
    }

    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        this.workflowDefinition = workflowDefinition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
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

    public List<TaskRun> getTaskRuns() {
        return taskRuns;
    }

    public void setTaskRuns(List<TaskRun> taskRuns) {
        this.taskRuns = taskRuns;
    }

    @Override
    public String toString() {
        return "WorkflowRun{" +
                "id=" + id +
                ", workflowDefinitionId=" + workflowDefinition +
                ", status=" + status +
                ", inputData='" + inputData + '\'' +
                ", outputData='" + outputData + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", taskRuns=" + taskRuns +
                '}';
    }
}
