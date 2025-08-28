package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.request.FcmTokenRequestDTO;
import com.tekcit.festival.domain.host_admin.service.FcmService;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Tag(name = "FCM 토큰 발급", description = "로그인 시 userId에 따른 FCM 토큰을 발급, 저장")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FcmTokenController {

    private final UserRepository userRepository;
    private final FcmService fcmService; // FcmService 의존성 주입

    // FCM 토큰 저장/갱신
    @PostMapping("/fcm-token")
    public ResponseEntity<String> receiveToken(
            @RequestBody FcmTokenRequestDTO requestDto,
            Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        String userIdStr = (String) authentication.getPrincipal();
        Long userId = Long.valueOf(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "존재하지 않는 사용자 ID: " + userId));

        fcmService.saveToken(user, requestDto.getToken());

        return ResponseEntity.ok("토큰 저장 완료");
    }

    // FCM 토큰 유효성 테스트용 임시 API
    @GetMapping("/fcm/token")
    public String testFcmToken(@RequestParam String token) {
        log.info("FCM 토큰 유효성 테스트 시작. 대상 토큰: {}", token);
        fcmService.validateTokenAndSend(token);
        return "FCM 토큰 테스트 시작. 로그를 확인하세요.";
    }
}