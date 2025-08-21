package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.request.AddressRequestDTO;
import com.tekcit.festival.domain.user.dto.response.AddressDTO;
import com.tekcit.festival.domain.user.service.AddressService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "주소 API", description = "주소 조회, 추가, 수정, 삭제, 기본 배송지 수정")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "회원 주소 정보 추가",
            description = "회원 주소 정보 추가, AddressRequestDTO를 포함해야 합니다. ex) POST /api/addresses/addAddress")
    public ResponseEntity<SuccessResponse<AddressDTO>> addAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO, @AuthenticationPrincipal(expression = "user.userId") Long userId){
        AddressDTO addressDTO = addressService.addAddress(addressRequestDTO, userId);
        return ApiResponseUtil.success(addressDTO);
    }

    @PatchMapping(value="/updateAddress/{addressId}")
    @Operation(summary = "회원 주소 정보 수정",
            description = "회원 주소 정보 수정, AddressRequestDTO를 포함해야 합니다. ex) PATCH /api/addresses/updateAddress")
    public ResponseEntity<SuccessResponse<AddressDTO>> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO, @AuthenticationPrincipal(expression = "user.userId") Long userId){
        AddressDTO addressDTO = addressService.updateAddress(addressId, addressRequestDTO, userId);
        return ApiResponseUtil.success(addressDTO);
    }

    @PatchMapping(value="/changeDefault/{addressId}")
    @Operation(summary = "회원 주소 기본 배송지 수정",
            description = "회원 주소 기본 배송지 수정, ex) PATCH /api/addresses/changeDefault/{addressId}")
    public ResponseEntity<SuccessResponse<AddressDTO>> updateDefault(@PathVariable Long addressId, @AuthenticationPrincipal(expression = "user.userId") Long userId){
        AddressDTO addressDTO = addressService.updateDefault(addressId, userId);
        return ApiResponseUtil.success(addressDTO);
    }

    @DeleteMapping(value="/{addressId}")
    @Operation(summary = "회원 주소 삭제",
            description = "회원 주소 삭제, ex) DELETE /api/addresses/deleteAddress/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal(expression = "user.userId") Long userId){
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "회원 주소 정보 조회",
            description = "회원 주소 정보 조회 ex) GET /api/addresses")
    public ResponseEntity<SuccessResponse<List<AddressDTO>>> getAddresses(@AuthenticationPrincipal(expression = "user.userId") Long userId){
        List<AddressDTO> addressDTOS = addressService.getAddresses(userId);
        return ApiResponseUtil.success(addressDTOS);
    }

}
