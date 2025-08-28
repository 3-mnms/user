package com.tekcit.festival.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "이메일 인증 응답 DTO", name = "EmailResponseDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResponseDTO {

    @Schema(description = "성공 여부(true, false)")
    private boolean success;

    @Schema(description = "응답 메시지")
    private String message;
}
