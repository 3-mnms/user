package com.tekcit.festival.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${base.service.url}")
    private String baseServiceUrl;

    @Bean
    public WebClient bookingWebClient() {
        return WebClient.builder()
                .baseUrl(baseServiceUrl)
                .build();
    }
}