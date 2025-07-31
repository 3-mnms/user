package com.tekcit.festival.domain.user.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Mock 처리용 (나중에 삭제 예정)
@Slf4j
@Component
public class FirebaseFcmSender implements FcmSender {

    @Override
    public void send(String targetToken, String title, String body) {
        try {
            AndroidNotification androidNotification = AndroidNotification.builder()
                    .setSound("default")
                    .setDefaultSound(true)
                    .setDefaultVibrateTimings(true)
                    .setPriority(AndroidNotification.Priority.HIGH)
                    .build();

            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setNotification(androidNotification)
                    .build();

            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .setAndroidConfig(androidConfig)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM 메시지 전송 성공: title={}, response={}", title, response);
        } catch (Exception e) {
            log.error("❌ FCM 메시지 전송 실패", e);
        }
    }
}
