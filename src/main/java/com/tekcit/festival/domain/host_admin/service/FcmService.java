package com.tekcit.festival.domain.host_admin.service;

import com.google.firebase.messaging.*;
import com.tekcit.festival.domain.host_admin.entity.FcmToken;
import com.tekcit.festival.domain.host_admin.repository.FcmTokenRepository;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    // 발송 실패 시 유효하지 않은 토큰을 DB에서 삭제합니다.
    @Transactional
    public void sendMessageToUsers(List<Long> userIds, String title, String body) {
        List<String> tokens = fcmTokenRepository.findTokensByUserIds(userIds);

        if (tokens.isEmpty()) {
            log.warn("전송할 FCM 토큰 없음 - 메시지 미전송");
            return;
        }

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body).build())
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
            log.info("FCM 멀티캐스트 메시지 전송 결과: 총 {}개, 성공 {}개, 실패 {}개",
                    response.getResponses().size(), response.getSuccessCount(), response.getFailureCount());

            // 유효하지 않은 토큰들을 데이터베이스에서 삭제
            if (response.getFailureCount() > 0) {
                Set<String> failedTokens = response.getResponses().stream()
                        .filter(r -> !r.isSuccessful())
                        .map(r -> r.getException().getMessage().split(" ")[0])
                        .collect(Collectors.toSet());

                log.warn("전송 실패한 유효하지 않은 토큰들을 DB에서 삭제합니다: {}", failedTokens);
                fcmTokenRepository.deleteAllByTokenIn(failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 메시지 전송 실패", e);
            throw new BusinessException(ErrorCode.FCM_SEND_FAILED);
        }
    }

    // FCM 토큰을 저장하거나 이미 존재하는 경우 갱신합니다.
    @Transactional
    public void saveToken(User user, String token) {
        // 기존 토큰을 찾고, 없다면 새로운 엔티티를 생성합니다.
        FcmToken fcmTokenToSave = fcmTokenRepository.findByUser(user)
                .map(existing -> {
                    // 기존 토큰이 존재하면 값만 업데이트하고 반환
                    existing.setToken(token);
                    return existing;
                })
                .orElseGet(() -> FcmToken.builder().user(user).token(token).build());

        // 새로운 엔티티이거나 업데이트된 엔티티를 모두 명시적으로 저장
        fcmTokenRepository.save(fcmTokenToSave);
    }

    // 특정 FCM 토큰이 유효한지 테스트하기 위해 단일 알림을 전송합니다.
    public void validateTokenAndSend(String targetToken) {
        try {
            Message message = Message.builder()
                    .setToken(targetToken)
                    .putData("title", "토큰 유효성 테스트")
                    .putData("body", "이 알림을 받았다면 토큰이 유효합니다.")
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✔️ 토큰 유효성 테스트 성공. 응답 ID: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("❌ 토큰 유효성 테스트 실패. 원인: {}", e.getMessage());
        }
    }
}