# NexusFlow: A Decentralized Workflow Orchestrator

NexusFlow is a powerful backend platform designed to run, manage, and monitor complex, multi-step automated processes. Built on a modern microservice architecture, it allows developers to define a workflow as a series of connected steps (a graph), and the system will automatically execute each step in the correct order, handle failures with automated retries, and report on the progress in real-time.

It serves as a lightweight, event-driven alternative to enterprise tools like Apache Airflow or AWS Step Functions, making it ideal for applications requiring high resilience and scalability.

-----

## ✨ Features

* **Event-Driven Microservice Architecture:** Core components (`auth-service`, `workflow-manager`, `task-runner`) are fully decoupled, communicating asynchronously via Apache Kafka for maximum resilience.
* **Complex Workflow Management:** Supports defining and executing multi-step workflows with complex dependencies using a Directed Acyclic Graph (DAG) model, including cycle detection to prevent invalid workflows.
* **Dynamic Task Execution:** A generic `task-runner` service can execute different types of jobs based on the workflow definition, allowing for a flexible and extensible system.
* **Robust Error Handling & Retries:** Automatically retries failed tasks a configurable number of times before marking a workflow as failed, ensuring high reliability.
* **Secure API:** A dedicated authentication service issues JWTs, and a Traefik API Gateway protects all endpoints, ensuring only authorized users can access the system.
* **Containerized Environment:** The entire multi-service application stack (including PostgreSQL, Kafka, and MinIO) is containerized with Docker for consistent and portable deployments.
* **Artifact & Log Storage:** Fully integrates with MinIO object storage to save and retrieve detailed task logs and any output files ("artifacts").

-----

## 🏗️ Architecture

The system is composed of several independent microservices that work together:

* **API Gateway (Traefik):** The single entry point for all external traffic, routing requests to the appropriate service.
* **Auth Service:** Manages user registration and login, issuing JWTs for authentication.
* **Workflow Manager:** The "brain" of the system. It defines workflows, tracks the state of each run, and orchestrates the overall process by publishing events.
* **Task Runner:** A stateless worker that listens for task execution events, performs the business logic, and reports the result.
* **Backing Services:** The platform relies on Kafka for event streaming, PostgreSQL for state persistence, and MinIO for object storage.

-----

## 🚀 How to Run

### Prerequisites

* [Docker](https://www.docker.com/get-started) and Docker Compose
* [Apache Maven](https://maven.apache.org/download.cgi)
* Java 17 or higher

### 1\. Set Up Environment Variables

Create a `.env` file in the project's root directory. This file stores all your secret credentials.

> ```env
> # PostgreSQL Credentials
> NEXUSFLOW_POSTGRES_USER=your_postgres_user
> NEXUSFLOW_POSTGRES_PASSWORD=your_postgres_password
> 
>  #  MinIO Credentials
> NEXUSFLOW\_MINIO\_ROOT\_USER=your\_minio\_user
> NEXUSFLOW\_MINIO\_ROOT\_PASSWORD=your\_minio\_password
> 
> # JWT Settings
> NEXUSFLOW\_JWT\_SECRET=your\_super\_long\_and\_secure\_base64\_encoded\_secret\_key
> NEXUSFLOW\_JWT\_EXPIRATION\_MS=86400000
> ```

### 2\. Build and Run the Application

From the root directory of the project, run the following commands in your terminal:

```bash
# First, build the Java applications using Maven
mvn clean install

# Then, start all services using Docker Compose.
# The --build flag ensures images are rebuilt if you've made changes.
docker-compose up --build
```

The application will be accessible through the Traefik API Gateway at `http://localhost:80`. You can view the Traefik dashboard at `http://localhost:8080`.

### Troubleshooting

**Environment Variable Issues in IDE:** If you run the services directly from your IDE and encounter errors related to missing environment variables, it's because the IDE's run configuration doesn't automatically see the root .env file. To fix this, you must manually add the required environment variables to the Run Configuration for each service (auth-service, workflow-manager, task-runner) within your IDE.

-----

## 🛠️ How to Use It

### API Endpoints

All endpoints are accessed through the API Gateway at `http://localhost:80`.

#### Authentication (`/api/auth`)

* **`POST /api/auth/register`**: Creates a new user account.
  * **Body:** `{ "username": "user", "password": "password" }`
* **`POST /api/auth/login`**: Authenticates a user and returns a JWT.
  * **Body:** `{ "username": "user", "password": "password" }`
  * **Response:** `{ "token": "your_jwt_here" }`

#### Workflows (`/api/workflows`)

*Requires a valid JWT in the `Authorization: Bearer <token>` header.*

* **`POST /api/workflows`**: Creates a new workflow definition.
  * **Body:** A JSON object representing the workflow (see sample below).
* **`POST /api/workflows/{id}/run`**: Triggers a new run of a specific workflow.

### Sample Workflow Definition

This example defines a three-step video processing pipeline where the final step depends on two parallel tasks completing first.

```json
{
  "name": "Video Processing Pipeline",
  "version": 1,
  "description": "Transcodes a video into multiple resolutions and generates thumbnails.",
  "maxRetries": 3,
  "definition": "{\"tasks\":[{\"name\":\"upload-video\",\"dependsOn\":[]},{\"name\":\"transcode-1080p\",\"dependsOn\":[\"upload-video\"]},{\"name\":\"transcode-480p\",\"dependsOn\":[\"upload-video\"]},{\"name\":\"package-for-streaming\",\"dependsOn\":[\"transcode-1080p\",\"transcode-480p\"]}]}"
}
```

### Adding Custom Tasks
The task-runner service uses a dynamic dispatcher to execute tasks. This means that only tasks with a corresponding handler defined in the task-runner will run. The name of the task in the workflow definition must match the name returned by the getTaskName() method in a handler.

If you need to add a new custom task:

1. Clone the repository.

2. In the task-runner service, create a new Java class in the handler package that implements the TaskHandler interface.

3. Implement the getTaskName() and execute() methods with your custom logic.

4. Re-build the Docker images (docker-compose up --build). Your new task is now available to be used in workflow definitions.

-----

## 🔧 Future Enhancements

* [ ] **Implement Full Observability:**
  * [ ] Instrument services with **Prometheus** for metrics collection.
  * [ ] Set up **Loki** for centralized log aggregation.
  * [ ] Implement distributed tracing with **Jaeger/Tempo**.
  * [ ] Create a comprehensive **Grafana dashboard** to visualize all metrics, logs, and traces.
* [ ] **Deploy to Kubernetes:**
  * [ ] Create Kubernetes manifests (Deployments, Services, etc.) for the entire application stack.
  * [ ] Set up a CI/CD pipeline (e.g., using GitHub Actions) to automate builds and deployments.
* [ ] **Advanced Features:**
  * [ ] Implement a real log-capturing utility to stream task `stdout`/`stderr` to MinIO.
  * [ ] Implement compensation logic (**Sagas**) for workflows that fail midway, allowing for clean rollbacks.
  * [ ] Develop a user-friendly web interface for managing and monitoring workflows.