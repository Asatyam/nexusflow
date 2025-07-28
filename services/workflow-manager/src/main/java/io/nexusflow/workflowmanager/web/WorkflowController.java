package io.nexusflow.workflowmanager.web;

import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import io.nexusflow.workflowmanager.entity.WorkflowRun;
import io.nexusflow.workflowmanager.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping
    public Iterable<WorkflowDefinition> getAllWorkflows() {
        return workflowService.getAllWorkflows();
    }

    @PostMapping
    public WorkflowDefinition createWorkflow(@RequestBody WorkflowDefinition workflowDefinition) {
        return workflowService.createWorkflow(
                workflowDefinition.getName(),
                workflowDefinition.getDescription(),
                workflowDefinition.getDefinition(),
                workflowDefinition.getMaxRetries()
        );
    }

    @PostMapping("/run/{id}")
    public WorkflowRun runWorkflow(@PathVariable("id") String workflowRunId) {
        Long id = Long.parseLong(workflowRunId);
        return workflowService.runWorkflow(id);
    }
}
