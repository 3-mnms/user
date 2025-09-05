package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.response.KakaoAddressSearchDTO;
import com.tekcit.festival.domain.user.dto.response.KakaoMapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSearchService {
    @Value("${kakao.restapi-key}")
    private String restApiKey;

    @Value("${kakao.search-base-url}")
    private String baseUrl;

    private final WebClient webClient = WebClient.builder().build();

    public Optional<KakaoMapResponseDTO> geocodeAddress(String address) {
        log.info("address: {}", address);
        if (address == null || address.isBlank())
            return Optional.empty();

        KakaoAddressSearchDTO response = webClient.get()
                .uri(baseUrl + "/v2/local/search/address.json?query={query}&size={size}", address, 1)
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .bodyToMono(KakaoAddressSearchDTO.class)
                .block();

        log.info("kakao response={}", response);

        if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
            return Optional.empty();
        }

        KakaoMapResponseDTO kakaoMapResponseDTO = response.getDocuments().get(0);
        return Optional.of(kakaoMapResponseDTO);
    }
}
