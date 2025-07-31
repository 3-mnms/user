package com.tekcit.festival.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Mock 처리용 (나중에 삭제 예정)
@Slf4j
@Component
@Profile("test") // 테스트 환경에서만 활성화
public class MockFcmSender implements FcmSender {
    @Override
    public void send(String targetToken, String title, String body) {
        log.info("📦 [MOCK] FCM 메시지 전송됨: {}, {}, {}", targetToken, title, body);
    }
}
