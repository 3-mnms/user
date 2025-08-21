package com.tekcit.festival.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "기존 비밀번호 일치 여부 확인 DTO", name = "CheckPWDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPwDTO {
    @Schema(description = "기존 로그인 비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String loginPw;
}
