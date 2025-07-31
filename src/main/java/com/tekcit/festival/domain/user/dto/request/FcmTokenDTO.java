package com.tekcit.festival.domain.user.dto.request;

import lombok.Getter;

@Getter
public class FcmTokenDTO {
    private Long userId;
    private String token;
}
