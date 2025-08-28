package com.tekcit.festival.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bookingWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    // 다른 외부 서비스 호출을 할 수 있음
    // @Bean
    // public WebClient anotherWebClient() {
    //     return WebClient.builder()
    //            .baseUrl("http://another-service-url")
    //            .build();
    // }
}