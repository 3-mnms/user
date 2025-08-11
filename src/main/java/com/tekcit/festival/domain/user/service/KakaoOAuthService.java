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

import java.util.Optional;

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

    private final WebClient webClient = WebClient.builder().build();

    public String exchangeCodeForAccessToken(String code){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");   // curl -d "grant_type=authorization_code"
        form.add("client_id", clientId);                // curl -d "client_id=..."
        form.add("redirect_uri", redirectUri);          // curl --data-urlencode "redirect_uri=..."
        form.add("code", code);                         // curl -d "code=..."

        // WebClient로 POST 요청 보내기
        KakaoTokenResponse tokenResponse = webClient.post()
                .uri(tokenUri) // "https://kauth.kakao.com/oauth/token"
                .contentType(MediaType.APPLICATION_FORM_URLENCODED) // curl -H "Content-Type: ..."
                .body(BodyInserters.fromFormData(form)) // 위에서 만든 form 데이터 넣기
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        if (tokenResponse == null || tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken().isBlank()) {
            throw new IllegalStateException("카카오 토큰 발급 실패: access_token 누락/빈 값");
        }

        return tokenResponse.getAccessToken();
    }

    public String fetchEmail(String accessToken){
        KakaoMeResponse me = webClient.get()
                .uri(userinfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoMeResponse.class)
                .block();

        KakaoMeResponse.KakaoAccount account = Optional.ofNullable(me)
                .map(KakaoMeResponse::getKakaoAccount)
                .orElse(null);

        if(account == null)
            throw new IllegalStateException("카카오 계정 정보가 비었습니다.");

        String email = account.getEmail();

        if (email == null || email.isBlank())
          throw new IllegalStateException("카카오 이메일을 받지 못했습니다. (scope/동의 확인 필요) ");

        return email;
    }
}

