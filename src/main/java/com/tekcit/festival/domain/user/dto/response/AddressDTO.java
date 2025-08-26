package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 주소 응답 DTO", name = "AddressDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    @Schema(description = "주소 Id")
    private Long id;

    @Schema(description = "수령자 이름")
    private String name;

    @Schema(description = "수령자 전화번호")
    private String phone;

    @Schema(description = "사용자 주소")
    private String address;

    @Schema(description = "사용자 주소 우편번호")
    private String zipCode;

    @Schema(description = "사용자 주소 기본 배송지 여부")
    private boolean isDefault;

    @Schema(hidden = true)
    public static AddressDTO fromEntity(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .name(address.getName())
                .phone(address.getPhone())
                .address(address.getAddress())
                .zipCode(address.getZipCode())
                .isDefault(address.isDefault())
                .build();
    }
}
