package com.tekcit.festival.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Schema(description = "AccessToken 정보 DTO", name = "AccessTokenInfoDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenInfoDTO {
    @Schema(description = "사용자 userId(Long), subject")
    private Long userId;

    @Schema(description = "사용자 Role(권한)")
    private String role;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "토큰 생성 시간")
    private Date issuedAt;

    @Schema(description = "토큰 만료 시간")
    private Date expiresAt;

    @Schema(description = "토큰 만료 여부")
    private boolean isExpired;
}
