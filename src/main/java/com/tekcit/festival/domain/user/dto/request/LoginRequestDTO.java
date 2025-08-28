package com.tekcit.festival.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 DTO", name = "LoginRequestDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    @Schema(description = "로그인 아이디")
    @NotBlank(message = "아이디는 필수 입력사항 입니다.")
    private String loginId;

    @Schema(description = "로그인 비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String loginPw;
}
