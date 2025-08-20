package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.host_admin.repository.FcmTokenRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    /**
     * FCM 토큰을 전달받아 저장하거나 갱신합니다.
     * 이 API는 프론트엔드에서 로그인 시 또는 토큰이 갱신될 때마다 호출됩니다.
     * userId는 게이트웨이나 JWT 필터에서 추출되어 헤더에 추가되는 것을 가정합니다.
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<String> receiveToken(
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") Long userId) {

        String token = body.get("token");

        // 유효한 사용자 ID인지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 사용자 ID: " + userId));

        // 기존 토큰이 있는지 확인하고 있으면 갱신, 없으면 새로 저장
        fcmTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        fcmToken -> {
                            fcmToken.setToken(token); // 토큰 갱신
                            fcmTokenRepository.save(fcmToken);
                        },
                        () -> {
                            FcmToken fcmToken = new FcmToken();
                            fcmToken.setUser(user);
                            fcmToken.setToken(token);
                            fcmTokenRepository.save(fcmToken);
                        }
                );

        System.out.println("✅ FCM 토큰 저장/갱신 완료 - userId: " + userId + ", token: " + token);
        return ResponseEntity.ok("토큰 저장 완료");
    }
}