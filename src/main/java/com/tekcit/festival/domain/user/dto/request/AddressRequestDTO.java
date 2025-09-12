package com.tekcit.festival.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원 배송지 추가, 수정 요청 DTO", name = "AddressRequestDTO")
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
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phone;

    //isDefault로 인식하도록
    @JsonProperty("isDefault")
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
