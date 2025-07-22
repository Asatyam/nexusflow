package io.nexusflow.enums;

public enum TaskCompletionStatus {
    SUCCESS("Success"),
    FAILURE("Failure"),
    IN_PROGRESS("In Progress"),
    CANCELLED("Cancelled");

    private final String status;

    TaskCompletionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
