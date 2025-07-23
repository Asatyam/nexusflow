## State Recovery and Event Sourcing

- Workflow Manager is stateful; Needs to know the status of all running workflows.
- `tasks.results` is an immutable log of all task results.
- When the Workflow Manager starts, it reads the `tasks.results` log to reconstruct the state of all workflows.
- This allows the system to recover from crashes or restarts without losing workflow state.
- With RabbitMQ, once the `tasks.results`  is acknowledged, it is removed from the queue, making it impossible to recover the state after a crash.
- This would mean the workflow will be left in pending state in case the Workflow Manager crashes after sending the task to the Task Runner but before receiving the result.

## Future Use Case

- Can easily add new services that consume from the `tasks.results` topic.
- For example, analytics or audit services can subscribe to the topics to analyze task performance or track workflow execution history.