package io.nexusflow.workflowmanager.enums;

public enum TaskRunStatusEnum {
    PENDING("Pending"),
    RUNNING("Running"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String status;

    TaskRunStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
