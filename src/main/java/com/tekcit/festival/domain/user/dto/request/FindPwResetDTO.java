package com.tekcit.festival.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "비밀번호 찾기 재설정 요청 DTO", name = "FindPwResetDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPwResetDTO {
    @Schema(description = "사용자 로그인 아이디")
    @NotBlank(message = "아이디는 필수 입력사항 입니다.")
    private String loginId;

    @Schema(description = "사용자 이메일")
    @NotBlank(message = "이메일은 필수 입력사항 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "새로운 로그인 비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String loginPw;
}
