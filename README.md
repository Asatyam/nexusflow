# Nexusflow

## Run
- `mvn install`
- `docker-compose up -d`
- Update postgres username and password


## TODO

[x] Project Foundation & Setup

[x] Defined overall system architecture (Event-Driven Microservices).

[x] Selected the core technology stack (Java/Spring Boot, Kafka, PostgreSQL).

[x] Set up the Git repository with a multi-module Maven structure (services, libs).

[x] Established a local development environment using Docker Compose for backing services (Postgres, Kafka).

[x] Core Backend Implementation

[x] Defined and created the primary database models (JPA Entities for WorkflowDefinition, WorkflowRun, TaskRun).

[x] Implemented the data access layer (Spring Data JPA Repositories).

[x] Created the shared event-schemas library to define Kafka message contracts.

[x] Built the initial API and Service layers for the workflow-manager.

[x] Implemented a basic Kafka producer in the workflow-manager and a consumer in the task-runner.

[x] Successfully established the first end-to-end workflow loop (API call -> manager publishes event -> runner consumes event).

[ ] Enhance Core Workflow Logic

[ ] Implement DAG (Directed Acyclic Graph) parsing from the WorkflowDefinition's JSON to handle multi-step workflows with complex dependencies.

[ ] Enhance the task-runner to dynamically execute different types of tasks based on the event payload, rather than a single hardcoded action.

[ ] Build System Resilience & Features

[ ] Implement robust error handling and automated retry policies for failed tasks.

[ ] Fully integrate MinIO for storing and retrieving task logs and output artifacts.

[ ] Implement compensation logic (Sagas) for workflows that fail midway, allowing for clean rollbacks.

[ ] Add API Gateway & Security

[ ] Introduce an API Gateway (e.g., Traefik, Spring Cloud Gateway) to act as a single entry point for all API calls.

[ ] Secure the API endpoints with an authentication/authorization mechanism like JWT or OAuth2.

[ ] Implement Full Observability

[ ] Instrument all services with Prometheus to collect and expose key metrics (e.g., workflow duration, task failure rate).

[ ] Set up Loki and Promtail to aggregate logs from all services into a centralized, searchable system.

[ ] Implement distributed tracing (e.g., with OpenTelemetry and Jaeger/Tempo) to visualize the entire lifecycle of a request across multiple services.

[ ] Create a comprehensive Grafana dashboard to visualize all metrics, logs, and traces in one place.

[ ] Containerize & Prepare for Deployment

[ ] Write optimized Dockerfiles for the workflow-manager and task-runner Java applications.

[ ] Create Kubernetes manifests (Deployments, Services, etc.) to deploy the entire application stack.

[ ] Set up a CI/CD pipeline (e.g., using GitHub Actions) to automate the building of Docker images and deployment to Kubernetes.