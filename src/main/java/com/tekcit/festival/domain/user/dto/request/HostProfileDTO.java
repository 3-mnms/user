package com.tekcit.festival.domain.user.dto.request;

import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "축제 주최측 정보 DTO", name = "HostProfileDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostProfileDTO {
    @Schema(description = "주최측 사업체 명")
    @NotBlank(message = "사업명은 필수 입력사항 입니다.")
    private String businessName;

    @Schema(description = "주최측 행사 장르")
    private String genre;

    public HostProfile toEntity(){
        return HostProfile.builder()
                .businessName(businessName)
                .genre(genre)
                .build();
    }
}
