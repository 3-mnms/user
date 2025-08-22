package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Schema(description = "마이페이지 사용자 조회 공통 DTO", name = "MyPageCommonDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyPageCommonDTO {
    @Schema(description = "로그인 아이디")
    private String loginId;

    @Schema(description = "회원 이름")
    private String name;

    @Schema(description = "회원 전화번호")
    private String phone;

    @Schema(description = "회원 이메일")
    private String email;

    @Schema(description = "회원 권한(USER, HOST, ADMIN")
    private UserRole role;

    @Schema(description = "회원 가입 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "회원 정보 수정 날짜")
    private LocalDateTime updatedAt;

    @Schema(description = "회원 가입 방법(LOCAL or KAKAO)")
    private OAuthProvider oauthProvider;

    @Schema(hidden = true)
    public static MyPageCommonDTO fromAdminEntity(User user) {
        return MyPageCommonDTO.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .oauthProvider(user.getOauthProvider())
                .build();
    }
}
