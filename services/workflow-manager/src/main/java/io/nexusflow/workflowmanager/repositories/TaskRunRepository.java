package io.nexusflow.workflowmanager.repositories;

import io.nexusflow.workflowmanager.entity.TaskRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRunRepository extends JpaRepository<TaskRun, Long> {
}
