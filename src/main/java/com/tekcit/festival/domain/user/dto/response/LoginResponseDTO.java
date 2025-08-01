package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 응답 DTO", name = "LoginResponseDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    @Schema(description = "사용자 DB 식별자")
    private Long userId;

    @Schema(description = "사용자의 로그인 ID")
    private String loginId;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "사용자 role", example = "USER")
    private String role;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    public static LoginResponseDTO fromUserAndToken(User user, String accessToken) {
        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .build();
    }

}
