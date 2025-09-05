package com.tekcit.festival.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "카카오 search 주소 응답 DTO", name = "KakaoMapResponseDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoMapResponseDTO {
    @JsonProperty("x")
    private String longitude;

    @JsonProperty("y")
    private String latitude;
}
