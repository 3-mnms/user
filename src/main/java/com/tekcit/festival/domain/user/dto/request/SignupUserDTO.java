package com.tekcit.festival.domain.user.dto.request;

import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원 생성 DTO", name = "SignupUserDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupUserDTO {
    @Schema(description = "로그인 아이디")
    @NotBlank(message = "아이디는 필수 입력사항 입니다.")
    private String loginId;

    @Schema(description = "로그인 비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String loginPw;

    @Schema(description = "회원 이름")
    @NotBlank(message = "이름은 필수 입력사항 입니다.")
    private String name;

    @Schema(description = "회원 전화번호")
    @NotBlank(message = "전화번호는 필수 입력사항 입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phone;

    @Schema(description = "회원 이메일")
    @NotBlank(message = "이메일은 필수 입력사항 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private UserProfileDTO userProfile; // USER일 때만

    private HostProfileDTO hostProfile; // HOST일 때만

    public User toUserEntity(){
        return User.builder()
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .phone(phone)
                .email(email)
                .role(UserRole.USER)
                .build();
    }

    public User toHostEntity(){
        return User.builder()
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .phone(phone)
                .email(email)
                .role(UserRole.HOST)
                .build();
    }


}
