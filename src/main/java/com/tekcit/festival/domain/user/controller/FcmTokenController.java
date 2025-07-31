package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.repository.FcmTokenRepository;
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

    @PostMapping("/fcm-token")
    public ResponseEntity<String> receiveToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");

        Long testUserId = 123L; // ✅ 추후 JWT에서 추출 예정

        User user = userRepository.findById(testUserId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 사용자"));

        FcmToken fcmToken = new FcmToken();
        fcmToken.setUser(user); // ✅ 연관관계 설정
        fcmToken.setToken(token);

        fcmTokenRepository.save(fcmToken);

        System.out.println("✅ 저장 완료 - userId: " + user.getId() + ", token: " + token);
        return ResponseEntity.ok("토큰 저장 완료");
    }
}
