package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.request.FcmTokenRequestDTO;
import com.tekcit.festival.domain.host_admin.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.host_admin.repository.FcmTokenRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "FCM 토큰 발급", description = "로그인 시 userId에 따른 FCM 토큰을 발급, 저장")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    // FCM 토큰 저장/갱신, API 클라이언트(프론트)가 로그인 후 발급받은 FCM 토큰을 전달하면 DB에 저장, 동일 유저에게 기존 토큰이 있으면 갱신, 없으면 신규 저장
    @PostMapping("/fcm-token")
    public ResponseEntity<String> receiveToken(
            @RequestBody FcmTokenRequestDTO requestDto,
            Authentication authentication) {

        // 인증 여부 확인
        //   - Spring Security에서 설정한 Authentication이 없으면 401 반환
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        // principal에서 userId 추출
        String userIdStr = (String) authentication.getPrincipal();
        Long userId = Long.valueOf(userIdStr);

        // principal가 Long일시,
        //Long userId = (Long) authentication.getPrincipal();

        //   - Gateway/Header/JWT 필터에서 넣어준 값이 Number 또는 String일 수 있으므로 안전하게 변환
        //Long userId = extractUserId(authentication.getPrincipal());

        // DB에 존재하는 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "존재하지 않는 사용자 ID: " + userId));

        String token = requestDto.getToken();

        // 기존 토큰이 있으면 갱신, 없으면 새로 생성
        FcmToken fcmToken = fcmTokenRepository.findByUser(user)
                .map(existing -> {
                    // 기존 토큰 갱신
                    existing.setToken(token);
                    return existing;
                })
                .orElseGet(() -> {
                    // 신규 유저 토큰 생성
                    FcmToken t = new FcmToken();
                    t.setUser(user);
                    t.setToken(token);
                    return t;
                });

        // DB에 최종 저장
        fcmTokenRepository.save(fcmToken);

        return ResponseEntity.ok("토큰 저장 완료");
    }

    // principal을 안전하게 Long 타입 userId로 변환하는 유틸 메서드
    /*private Long extractUserId(Object principal) {
        if (principal instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(String.valueOf(principal));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 principal 값");
        }
    }
     */
}
