# HttpMessageNotWritableException

# What exactly

When returning the JSON response from WorkflowController, the response body contains nested task runs which
in turn contains the details about the same task run and workflow run. This can lead to a circular reference.


Like this
```

{
    "id": 452,
    "workflowDefinition": {
        "id": 1,
        "name": "Daily Sales Report",
        "description": "Generates and uploads the daily sales report.",
        "version": 1,
        "definition": "{\"tasks\":[{\"name\":\"fetch-sales-data\",\"dependsOn\":[]},{\"name\":\"process-sales-data\",\"dependsOn\":[\"fetch-sales-data\"]},{\"name\":\"generate-pdf-report\",\"dependsOn\":[\"process-sales-data\"]}]}",
        "createdAt": "2025-07-22T18:40:53.043339",
        "updatedAt": "2025-07-22T18:40:53.043391",
        "enabled": true
    },
    "status": "RUNNING",
    "inputData": null,
    "outputData": null,
    "startTime": "2025-07-23T18:38:52.825253",
    "endTime": null,
    "taskRuns": [
        {
            "id": 452,
            "workflowRun": {
                "id": 452,
                "workflowDefinition": {
                    "id": 1,
                    "name": "Daily Sales Report",
                    "description": "Generates and uploads the daily sales report.",
                    "version": 1,
                    "definition": "{\"tasks\":[{\"name\":\"fetch-sales-data\",\"dependsOn\":[]},{\"name\":\"process-sales-data\",\"dependsOn\":[\"fetch-sales-data\"]},{\"name\":\"generate-pdf-report\",\"dependsOn\":[\"process-sales-data\"]}]}",
                    "createdAt": "2025-07-22T18:40:53.043339",
                    "updatedAt": "2025-07-22T18:40:53.043391",
                    "enabled": true
                },
                "status": "RUNNING",
                "inputData": null,
                "outputData": null,
                "startTime": "2025-07-23T18:38:52.825253",
                "endTime": null,
                "taskRuns": [
                    {
                        "id": 452,
                        "workflowRun": {
                            "id": 452,
                            "workflowDefinition": {
                                "id": 1,
                                "name": "Daily Sales Report",
                                "description": "Generates and uploads the daily sales report.",
                                "version": 1,
                                "definition": "{\"tasks\":[{\"name\":\"fetch-sales-data\",\"dependsOn\":[]},{\"name\":\"process-sales-data\",\"dependsOn\":[\"fetch-sales-data\"]},{\"name\":\"generate-pdf-report\",\"dependsOn\":[\"process-sales-data\"]}]}",
                                "createdAt": "2025-07-22T18:40:53.043339",
                                "updatedAt": "2025-07-22T18:40:53.043391",
                                "enabled": true
                            },
                            "status": "RUNNING",
                            "inputData": null,
                            "outputData": null,
                            "startTime": "2025-07-23T18:38:52.825253",
                            "endTime": null,
                            "taskRuns": [
                                {
                                    "id": 452,
                                    "workflowRun": {
                                        "id": 452,
                                        "workflowDefinition": {
                                            "id": 1,
                                            "name": "Daily Sales Report",
                                            "description": "Generates and uploads the daily sales report.",
                                            "version": 1,
                                            "definition": "{\"tasks\":[{\"name\":\"fetch-sales-data\",\"dependsOn\":[]},{\"name\":\"process-sales-data\",\"dependsOn\":[\"fetch-sales-data\"]},{\"name\":\"generate-pdf-report\",\"dependsOn\":[\"process-sales-data\"]}]}",
                                            "createdAt": "2025-07-22T18:40:53.043339",
                                            "updatedAt": "2025-07-22T18:40:53.043391",
                                            "enabled": true
                                        },
                                        "status": "RUNNING",
                                        "inputData": null,
                                        "outputData": null,
                                        "startTime": "2025-07-23T18:38:52.825253",
                                        "endTime": null,
                                        "taskRuns": [
                                        ..........................
```

# Fix

Add `JsonManagedReference` and `JsonBackReference` annotations to the `WorkflowRun` and `TaskRun` entities to break the circular reference.
