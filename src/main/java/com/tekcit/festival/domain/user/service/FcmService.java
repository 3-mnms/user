package com.tekcit.festival.domain.user.service;

import com.google.firebase.messaging.*;
import com.tekcit.festival.domain.user.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.repository.FcmTokenRepository;
import com.tekcit.festival.domain.user.service.FcmSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmSender fcmSender; // 🔁 실제 구현체 또는 Mock 주입 가능
    private final FcmTokenRepository fcmTokenRepository;

    // ✅ 단일 토큰으로 메시지 전송
    public void sendMessage(String targetToken, String title, String body) {
        fcmSender.send(targetToken, title, body);
    }

    // ✅ 특정 사용자에게 메시지 전송
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
