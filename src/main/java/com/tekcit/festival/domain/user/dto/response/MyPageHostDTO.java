package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Schema(description = "마이페이지 축제 주최측 사용자 조회 DTO", name = "MyPageHostDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyPageHostDTO extends MyPageCommonDTO{

    //주최측 hostProfile
    @Schema(description = "주최측 사업체 명")
    private String businessName;

    @Schema(hidden = true)
    public static MyPageHostDTO fromHostEntity(User user, HostProfile hostProfile) {
        return MyPageHostDTO.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .oauthProvider(user.getOauthProvider())
                .businessName(hostProfile.getBusinessName())
                .build();
    }
}
