package io.nexusflow.taskrunner.handler;

import io.minio.*;
import io.nexusflow.eventschemas.TaskCompletionEvent;
import io.nexusflow.eventschemas.TaskExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileUploadTaskHandler implements TaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadTaskHandler.class);
    private static final String TASK_NAME = "file-upload-task";
    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Autowired
    public FileUploadTaskHandler(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public TaskCompletionEvent execute(TaskExecutionEvent event) throws Exception {
        LOGGER.info("Executing file upload task: {}", event.getTaskName());
        TaskCompletionEvent taskCompletionEvent = new TaskCompletionEvent();
        taskCompletionEvent.setTaskRunId(event.getTaskRunId());
        taskCompletionEvent.setWorkflowRunId(event.getWorkflowRunId());

        Path tempLogFile = null;
        try{
            String logContent = "File upload task started for workflow run: " + event.getWorkflowRunId();
            tempLogFile = Files.createTempFile("task-log-", ".txt");
            Files.writeString(tempLogFile, logContent);
            LOGGER.info("Temporary log file created at: {}", tempLogFile.toString());

            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("nexusflow-logs").build());
            if (!bucketExists) {
                LOGGER.info("Bucket 'nexusflow-logs' does not exist, creating it.");
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("nexusflow-logs").build());
            } else {
                LOGGER.info("Bucket 'nexusflow-logs' already exists.");
            }

            String logFileName = "logs/" + event.getWorkflowRunId() + "/task-log-" + event.getTaskRunId() + ".txt";
            minioClient.uploadObject(
                    UploadObjectArgs
                            .builder()
                            .bucket("nexusflow-logs")
                            .object(logFileName)
                            .filename(tempLogFile.toAbsolutePath().toString())
                            .build()
            );
            taskCompletionEvent.setStatus("SUCCESS");
            taskCompletionEvent.setMessage("File uploaded successfully to MinIO bucket 'nexusflow-logs'.");
            taskCompletionEvent.setLogsUrl(minioUrl + "/nexusflow-logs/" + logFileName);
            taskCompletionEvent.setArtifactUrl("https://example.com/artifacts/" + event.getTaskRunId());
            LOGGER.info("Log file uploaded to MinIO bucket 'nexusflow-logs' with name: {}", logFileName);

        } finally {
            if(tempLogFile != null) {
                try {
                    Files.deleteIfExists(tempLogFile);
                    LOGGER.info("Temporary log file deleted: {}", tempLogFile.toString());
                } catch (IOException e) {
                    LOGGER.error("Failed to delete temporary log file: {}", e.getMessage());
                }
            }
        }

        return taskCompletionEvent;

    }
}
