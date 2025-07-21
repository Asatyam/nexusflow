package io.nexusflow.workflowmanager.service;

import io.nexusflow.workflowmanager.entity.WorkflowDefinition;
import io.nexusflow.workflowmanager.repositories.TaskRunRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowDefinitionRepository;
import io.nexusflow.workflowmanager.repositories.WorkflowRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public WorkflowService(WorkflowDefinitionRepository workflowDefinitionRepository, WorkflowRunRepository workflowRunRepository, TaskRunRepository taskRunRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public WorkflowDefinition createWorkflow(String name,String description, String definition) {
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setName(name);
        workflowDefinition.setDefinition(definition);
        workflowDefinition.setDescription(description);
        workflowDefinition.setVersion(1);
        workflowDefinition.setEnabled(true);
        return workflowDefinitionRepository.save(workflowDefinition);
    }

}
