package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Schema(description = "마이페이지 사용자 수정 후 응답 DTO", name = "UpdateUserResponseDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateUserResponseDTO {
    @Schema(description = "로그인 아이디")
    private String loginId;

    @Schema(description = "회원 이름")
    private String name;

    @Schema(description = "회원 전화번호")
    private String phone;

    @Schema(description = "회원 정보 수정 날짜")
    private LocalDateTime updatedAt;

    @Schema(description = "회원 가입 방법(LOCAL or KAKAO)")
    private OAuthProvider oauthProvider;

    @Schema(description = "회원 주민번호 앞자리 + 뒷자리 첫자리")
    private String residentNum;

    @Schema(description = "회원 생년월일")
    private String birth;

    @Schema(description = "회원 성별")
    private UserGender gender;

    @Schema(description = "회원 나이")
    private int age;

    @Schema(hidden = true)
    public static UpdateUserResponseDTO fromUserEntity(User user, UserProfile userProfile) {
        return UpdateUserResponseDTO.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .updatedAt(user.getUpdatedAt())
                .oauthProvider(user.getOauthProvider())
                .residentNum(userProfile.getResidentNum())
                .birth(userProfile.getBirth())
                .gender(userProfile.getGender())
                .age(userProfile.getAge())
                .build();
    }
}
