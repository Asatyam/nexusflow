# Unable to Inject Values from Application Properties

## What Exactly Happened
When I tried to run my task-runner sub-module directly from my IDE, I realized its starting point was that sub-module's directory, not my project's root folder.

This meant the application couldn't see the .env file I had in the root directory. So, when Spring Boot started up, it failed immediately because it couldn't create my MinioClient bean. The @Value annotations in my MinioConfig class couldn't find the required values for placeholders like ${minio.access-key}, which caused the application startup to fail.

## The Fix
The solution was to explicitly tell my IDE's Run Configuration where to find the .env file for local development.

In IntelliJ IDEA, I did the following:
I went to Run > Edit Configurations....

I selected the run configuration for my TaskRunnerApplication and manually added the Environment Variables section.
Like this:
NEXUSFLOW_MINIO_ROOT_USER=user; NEXUSFLOW_MINIO_ROOT_PASSWORD = yoursecurepassword;

```

