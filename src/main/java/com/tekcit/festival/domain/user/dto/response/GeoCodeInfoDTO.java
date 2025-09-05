package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public static GeoCodeInfoDTO fromAddressEntity(Long userId, Address address) {
        return GeoCodeInfoDTO.builder()
                .userId(userId)
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
