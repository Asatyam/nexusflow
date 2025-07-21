package io.nexusflow.workflowmanager.entity;

import io.nexusflow.workflowmanager.enums.WorkflowRunStatusEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class WorkflowRun {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private WorkflowDefinition workflowDefinitionId;

    private WorkflowRunStatusEnum status;

    @Column(columnDefinition = "TEXT")
    private String inputData;
    private String outputData;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
