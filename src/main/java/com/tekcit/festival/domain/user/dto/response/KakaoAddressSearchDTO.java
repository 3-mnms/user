package com.tekcit.festival.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "카카오 주소 검색 전체 응답", name = "KakaoAddressSearchDTO")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressSearchDTO {
    private List<KakaoMapResponseDTO> documents;
}