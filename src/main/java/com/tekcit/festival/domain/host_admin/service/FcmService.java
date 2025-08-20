package com.tekcit.festival.domain.host_admin.service;

import com.google.firebase.messaging.*;
import com.tekcit.festival.domain.host_admin.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.host_admin.repository.FcmTokenRepository;
import com.tekcit.festival.domain.host_admin.service.FcmSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmSender fcmSender;
    private final FcmTokenRepository fcmTokenRepository;

    /**
     * 단일 토큰으로 FCM 메시지 전송
     */
    public void sendMessage(String targetToken, String title, String body) {
        fcmSender.send(targetToken, title, body);
    }

    /**
     * 특정 사용자에게 FCM 메시지 전송
     */
    public void sendMessageToUser(User user, String title, String body) {
        fcmTokenRepository.findByUser(user).ifPresentOrElse(
                tokenEntity -> sendMessage(tokenEntity.getToken(), title, body),
                () -> log.warn("⚠️ FCM 토큰 없음 -> 메시지 미전송: userId={}", user.getUserId())
        );
    }

    /**
     * 여러 사용자 ID에 해당하는 모든 FCM 토큰에 메시지 전송
     */
    public void sendMessageToUsers(List<Long> userIds, String title, String body) {
        // userId 목록에서 중복을 제거하고 유효한 FCM 토큰 목록을 가져옴
        List<String> tokens = fcmTokenRepository.findTokensByUserIds(userIds);

        if (tokens.isEmpty()) {
            log.warn("⚠️ 전송할 FCM 토큰 없음 - 메시지 미전송");
            return;
        }

        // Multicast 메시지 전송
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
            log.info("✅ FCM 멀티캐스트 메시지 전송 성공: 총 {}개, 성공 {}개, 실패 {}개",
                    response.getResponses().size(), response.getSuccessCount(), response.getFailureCount());

            // 실패한 토큰 관리 (옵션)
            if (response.getFailureCount() > 0) {
                Set<String> failedTokens = response.getResponses().stream()
                        .filter(r -> !r.isSuccessful())
                        .map(r -> {
                            System.err.println("메시지 전송 실패: " + r.getException().getMessage());
                            return r.getException().getMessage().split(" ")[0];
                        })
                        .collect(Collectors.toSet());

                log.warn("❌ 전송 실패한 토큰들: {}", failedTokens);
                // 이 실패한 토큰들을 DB에서 삭제하는 로직을 추가할 수 있습니다.
                // 예: fcmTokenRepository.deleteAllByTokenIn(failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.error("❌ FCM 멀티캐스트 메시지 전송 실패", e);
        }
    }

    /**
     * FCM 토큰 저장 또는 갱신
     */
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
        log.info("💾 FCM 토큰 저장 완료: userId={}, token={}", user.getUserId(), token);
    }
}