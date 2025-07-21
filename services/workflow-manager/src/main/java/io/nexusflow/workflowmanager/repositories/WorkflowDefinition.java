package io.nexusflow.workflowmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowDefinition extends JpaRepository<WorkflowDefinition, Long> {
}
