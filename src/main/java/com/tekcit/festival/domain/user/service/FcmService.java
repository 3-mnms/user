package com.tekcit.festival.domain.user.service;

import com.google.firebase.messaging.*;
import com.tekcit.festival.domain.user.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    // ✅ 단일 토큰으로 FCM 메시지 전송
    public void sendMessage(String targetToken, String title, String body) {
        try {
            // Android 알림 설정
            AndroidNotification androidNotification = AndroidNotification.builder()
                    .setSound("default")
                    .setDefaultSound(true)
                    .setDefaultVibrateTimings(true)
                    .setPriority(AndroidNotification.Priority.HIGH)
                    .build();

            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setNotification(androidNotification)
                    .build();

            // 전체 메시지 구성
            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(androidConfig)
                    .build();

            // 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM 메시지 전송 성공: title={}, token={}, response={}", title, targetToken, response);

        } catch (Exception e) {
            log.error("❌ FCM 메시지 전송 실패: token={}", targetToken, e);
        }
    }

    // ✅ 특정 사용자에게 메시지 전송 (user → token 조회 후 전송)
    public void sendMessageToUser(User user, String title, String body) {
        fcmTokenRepository.findByUser(user).ifPresentOrElse(
                tokenEntity -> sendMessage(tokenEntity.getToken(), title, body),
                () -> log.warn("⚠️ FCM 토큰 없음 → 메시지 미전송: userId={}", user.getId())
        );
    }

    // ✅ FCM 토큰 저장 또는 갱신
    public void saveToken(User user, String token) {
        FcmToken fcmToken = fcmTokenRepository.findByUser(user)
                .map(existing -> {
                    existing.setToken(token);
                    return existing;
                })
                .orElseGet(() -> {
                    FcmToken newToken = new FcmToken();
                    newToken.setUser(user);
                    newToken.setToken(token);
                    return newToken;
                });

        fcmTokenRepository.save(fcmToken);
        log.info("💾 FCM 토큰 저장 완료: userId={}, token={}", user.getId(), token);
    }
}
