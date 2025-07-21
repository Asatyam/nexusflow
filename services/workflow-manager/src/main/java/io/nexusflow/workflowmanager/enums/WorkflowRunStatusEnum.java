package io.nexusflow.workflowmanager.enums;

public enum WorkflowRunStatusEnum {
    PENDING("Pending"),
    RUNNING("Running"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    WorkflowRunStatusEnum(String status) {
    }
}
