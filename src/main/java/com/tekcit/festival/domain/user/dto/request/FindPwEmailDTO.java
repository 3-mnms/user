package com.tekcit.festival.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "비밀번호 찾기 요청 DTO", name = "FindPwEmailDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPwEmailDTO {
    @Schema(description = "사용자 로그인 아이디")
    @NotBlank(message = "아이디는 필수 입력사항 입니다.")
    private String loginId;

    @Schema(description = "사용자 이름")
    @NotBlank(message = "이름은 필수 입력사항 입니다.")
    private String name;
}
