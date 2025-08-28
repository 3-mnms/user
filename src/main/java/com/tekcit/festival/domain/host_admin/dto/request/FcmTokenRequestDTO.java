package com.tekcit.festival.domain.host_admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "FCM 토큰 요청 DTO")
public class FcmTokenRequestDTO {
    @Schema(description = "사용자 FCM 토큰", example = "fcm_token_example_12345")
    private String token;
}