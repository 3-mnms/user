package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "운영관리자 축제 주최측 전체 조회 DTO", name = "AdminHostListDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminHostListDTO {
    @Schema(description = "주최측 회원 이름")
    private String name;

    @Schema(description = "주최측 회원 로그인 아이디")
    private String loginId;

    @Schema(description = "주최측 회원 전화번호")
    private String phone;

    @Schema(description = "주최측 회원 이메일")
    private String email;

    @Schema(description = "주최측 회원 사업체 명")
    private String businessName;

    @Schema(description = "주최측 회원 계정 상태(true면 활성화)")
    private boolean isActive;

    @Schema(hidden = true)
    public static AdminHostListDTO fromUserEntity(User user) {
        HostProfile hostProfile = user.getHostProfile();

        return AdminHostListDTO.builder()
                .name(user.getName())
                .loginId(user.getLoginId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .businessName(hostProfile.getBusinessName())
                .isActive(hostProfile.isActive())
                .build();
    }
}
