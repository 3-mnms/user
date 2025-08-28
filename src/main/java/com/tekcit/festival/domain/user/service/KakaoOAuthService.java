package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.response.KakaoMeResponse;
import com.tekcit.festival.domain.user.dto.response.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoOAuthService {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.userinfo-uri}")
    private String userinfoUri;

    @Value("${kakao.admin-key}")
    private String adminKey;

    private final WebClient webClient = WebClient.builder().build();

    public String exchangeCodeForAccessToken(String code){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");   // curl -d "grant_type=authorization_code"
        form.add("client_id", clientId);                // curl -d "client_id=..."
        form.add("redirect_uri", redirectUri);          // curl --data-urlencode "redirect_uri=..."
        form.add("code", code);                         // curl -d "code=..."

        // WebClient로 POST 요청 보내기
        KakaoTokenResponse tokenResponse = webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        if (tokenResponse == null || tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken().isBlank()) {
            throw new IllegalStateException("카카오 토큰 발급 실패: access_token 누락/빈 값");
        }

        return tokenResponse.getAccessToken();
    }

    public KakaoMeResponse fetchMe(String accessToken){
        KakaoMeResponse me = webClient.get()
                .uri(userinfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoMeResponse.class)
                .block();

        if (me == null)
            throw new IllegalStateException("카카오 사용자 정보 조회 실패");

        return me;
    }

    public void unlinkByAdmin(String kakaoUserId) {
        webClient.post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("target_id_type", "user_id")
                        .with("target_id", kakaoUserId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

