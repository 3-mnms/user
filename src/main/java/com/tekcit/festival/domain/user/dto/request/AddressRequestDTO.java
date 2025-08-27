package com.tekcit.festival.domain.user.dto.request;

import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원 배송지 추가, 수정 요청 DTO", name = "AddAddressDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {
    @Schema(description = "회원 주소")
    @NotBlank(message = "주소는 필수 입력사항 입니다.")
    private String address;

    @Schema(description = "회원 주소 우편 번호")
    @NotBlank(message = "우편 번호는 필수 입력사항 입니다.")
    private String zipCode;

    @Schema(description = "수령자 이름")
    @NotBlank(message = "수령자 이름은 필수 입력사항 입니다.")
    private String name;

    @Schema(description = "수령자 전화번호")
    @NotBlank(message = "수령자 전화번호는 필수 입력사항 입니다.")
    private String phone;

    @Schema(description = "기본 배송지 여부")
    private boolean isDefault;

    public Address toAddressEntity(UserProfile userProfile){
        return Address.builder()
                .address(address)
                .zipCode(zipCode)
                .name(name)
                .phone(phone)
                .isDefault(isDefault)
                .userProfile(userProfile)
                .build();
    }
}
