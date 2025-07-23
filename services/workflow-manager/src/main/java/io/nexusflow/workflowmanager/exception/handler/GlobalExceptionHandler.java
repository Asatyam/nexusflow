package io.nexusflow.workflowmanager.exception.handler;

import io.nexusflow.workflowmanager.exception.InvalidWorkflowDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidWorkflowDefinitionException.class)
     public ResponseEntity<Map<String, Object>> handleInvalidWorkflowDefinitionException(InvalidWorkflowDefinitionException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid Workflow Definition");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("path", "/api/workflows");

        return  new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }
}
