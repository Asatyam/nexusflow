package io.nexusflow.workflowmanager.repositories;

import io.nexusflow.workflowmanager.entity.TaskRun;
import io.nexusflow.workflowmanager.entity.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRunRepository extends JpaRepository<TaskRun, Long> {
    List<TaskRun> findByWorkflowRunIdAndStatus(Long workflowRunId, String status);

    boolean existsByWorkflowRunAndTaskName(WorkflowRun workflowRun, String taskName);

    long countByWorkflowRunAndStatus(WorkflowRun workflowRun, String completed);
}
