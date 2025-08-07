package com.tekcit.festival.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventConsumer {

    @KafkaListener(
            topics = "${app.kafka.topic.user-event}",
            groupId = "user-event-group",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void consume(UserEventDTO event) {
        log.info("📥 Kafka 메시지 수신: {}", event);
    }
}
