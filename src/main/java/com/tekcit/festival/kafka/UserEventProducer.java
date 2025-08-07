package com.tekcit.festival.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;

    @Value("${app.kafka.topic.user-event}")
    private String topic;

    public void send(UserEventDTO dto) {
        kafkaTemplate.send(topic, dto.getLoginId(), dto);
        System.out.println("✅ UserEvent 전송 완료: " + dto);
    }
}