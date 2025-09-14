package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.enums.LoginStatus;
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

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "로그인 상태")
    private LoginStatus kind;

    public static LoginResponseDTO fromToken(String accessToken) {
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .kind(LoginStatus.SUCCESS)
                .build();
    }

}
