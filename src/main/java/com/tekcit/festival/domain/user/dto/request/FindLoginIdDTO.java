package com.tekcit.festival.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "아이디 찾기 요청 DTO", name = "FindLoginIdDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindLoginIdDTO {
    @Schema(description = "사용자 이름")
    @NotBlank(message = "이름은 필수 입력사항 입니다.")
    private String name;

    @Schema(description = "사용자 이메일")
    @NotBlank(message = "이메일은 필수 입력사항 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}