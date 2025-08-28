package com.tekcit.festival.domain.user.dto.request;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "KAKAO 회원 생성 요청 DTO", name = "KakaoSignupDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoSignupDTO {
    @Schema(description = "회원 이름")
    @NotBlank(message = "이름은 필수 입력사항 입니다.")
    private String name;

    @Schema(description = "회원 전화번호")
    @NotBlank(message = "전화번호는 필수 입력사항 입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phone;

    private UserProfileDTO userProfile; // USER일 때만

    public User toUserEntity(String kakaoId, String kakaoEmail){
        return User.builder()
                .loginId(null)
                .loginPw(null)
                .name(name)
                .phone(phone)
                .email(kakaoEmail)
                .isEmailVerified(true)
                .role(UserRole.USER)
                .oauthProviderId(kakaoId)
                .oauthProvider(OAuthProvider.KAKAO)
                .build();
    }
}

