package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Tag(name = "카카오 회원가입, 로그인 API", description = "카카오 회원가입, 로그인, 로그아웃, 토큰 재발급")
public class KakaoAuthController {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.authorize-uri}")
    private String authorizeUri;

    @Value("${kakao.scope}")
    private String scope;

    @GetMapping("/authorize")
    public void redirectToKakao(HttpServletResponse response) throws IOException {

        String url = UriComponentsBuilder.fromHttpUrl(authorizeUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .toUriString();

        response.sendRedirect(url); // 브라우저를 카카오로 리다이렉트
    }
}

