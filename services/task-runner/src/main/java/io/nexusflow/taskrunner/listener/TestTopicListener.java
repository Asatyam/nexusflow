package io.nexusflow.taskrunner.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestTopicListener {

    private static final Logger logger = LoggerFactory.getLogger(TestTopicListener.class);

    @KafkaListener(topics= "tasks.execute")
    public void listen(String message) {
        logger.info("Received message: {}", message);
    }
}
