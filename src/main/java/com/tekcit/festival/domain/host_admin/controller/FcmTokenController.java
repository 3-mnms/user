package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.request.FcmTokenRequestDTO;
import com.tekcit.festival.domain.host_admin.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.host_admin.repository.FcmTokenRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FCM 토큰 발급", description = "로그인 시 userId에 따른 FCM 토큰을 발급, 저장")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    // FCM 토큰을 전달받아 저장하거나 갱신, 이 API는 프론트엔드에서 로그인 시 호출
    @PostMapping("/fcm-token")
    public ResponseEntity<String> receiveToken(
            @RequestBody FcmTokenRequestDTO requestDto,
            @RequestHeader("X-User-Id") Long userId) {

        String token = requestDto.getToken();

        // 유효한 사용자 ID인지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 사용자 ID: " + userId));

        // 기존 토큰이 있으면 갱신하고, 없으면 새로 생성
        FcmToken fcmToken = fcmTokenRepository.findByUser(user)
                .map(existingToken -> {
                    existingToken.setToken(token);
                    return existingToken;
                })
                .orElseGet(() -> {
                    FcmToken newToken = new FcmToken();
                    newToken.setUser(user);
                    newToken.setToken(token);
                    return newToken;
                });

        // 최종 FcmToken 객체를 저장/갱신
        fcmTokenRepository.save(fcmToken);

        System.out.println("✅ FCM 토큰 저장/갱신 완료 - userId: " + userId + ", token: " + token);
        return ResponseEntity.ok("토큰 저장 완료");
    }
}