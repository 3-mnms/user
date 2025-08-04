package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.request.LoginRequestDTO;
import com.tekcit.festival.domain.user.dto.response.LoginResponseDTO;
import com.tekcit.festival.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "로그인 API", description = "회원 인증")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO loginResult = authService.login(request, response);
        return ResponseEntity.ok(loginResult);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDTO newToken = authService.reissue(request, response);
        return ResponseEntity.ok(newToken);
    }

    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo(Authentication authentication) {
            // SecurityContext에서 로그인한 사용자 정보 확인
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok("Hello, " + userDetails.getUsername());
    }
}
