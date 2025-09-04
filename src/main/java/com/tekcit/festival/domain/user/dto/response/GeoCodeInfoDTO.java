package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.GeocodeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "User GeoCode 정보 DTO", name = "GeoCodeInfoDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoCodeInfoDTO {
    @Schema(description = "사용자 userId")
    private Long userId;

    @Schema(description = "사용자 주소 위도")
    private Double latitude; //위도

    @Schema(description = "사용자 주소 경도")
    private Double longitude;//경도

    @Schema(hidden = true)
    public static GeoCodeInfoDTO fromUserProfileEntity(Long userId, UserProfile userProfile) {
        return GeoCodeInfoDTO.builder()
                .userId(userId)
                .latitude(userProfile.getLatitude())
                .longitude(userProfile.getLongitude())
                .build();
    }
}
