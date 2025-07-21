package io.nexusflow.workflowmanager.web;

import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import io.nexusflow.workflowmanager.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public WorkflowDefinition createWorkflow(@RequestBody WorkflowDefinition workflowDefinition) {
        return workflowService.createWorkflow(
                workflowDefinition.getName(),
                workflowDefinition.getDescription(),
                workflowDefinition.getDefinition()
        );
    }
}
