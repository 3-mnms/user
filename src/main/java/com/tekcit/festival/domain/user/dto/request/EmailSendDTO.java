package com.tekcit.festival.domain.user.dto.request;

import com.tekcit.festival.domain.user.entity.EmailVerification;
import com.tekcit.festival.domain.user.enums.VerificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "이메일 인증 코드 요청 DTO", name = "EmailSendRequestDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSendDTO {
    @Schema(description = "사용자 이메일")
    @NotBlank(message = "이메일은 필수 입력사항 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "검증 타입(SIGNUP(회원가입), EMAIL_UPDATE(이메일 변경), PASSWORD_FIND(비밀번호 찾기)")
    @NotNull(message = "검증 타입은 필수입니다.")
    private VerificationType type;

    public EmailVerification toEmailEntity(){
        return EmailVerification.builder()
                .email(email)
                .type(type)
                .build();
    }
}
