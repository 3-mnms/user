package com.tekcit.festival.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * WebClient 빈을 스프링 컨테이너에 등록합니다.
     * WebClient.Builder를 사용하여 커스터마이징된 WebClient를 생성할 수 있습니다.
     */
    @Bean
    public WebClient webClient() {
        // 기본 URL, 타임아웃 등 필요한 설정을 추가할 수 있습니다.
        return WebClient.builder()
                .build();
    }
}