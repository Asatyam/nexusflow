package io.nexusflow.workflowmanager.repositories;

import io.nexusflow.workflowmanager.entity.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRunRepository extends JpaRepository<WorkflowRun, Long> {
}
