package io.nexusflow.workflowmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWorkflowDefinitionException extends RuntimeException {
    public InvalidWorkflowDefinitionException(String message) {
        super(message);
    }

    public InvalidWorkflowDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
